package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;


@RestController
public class ShipController {
    private ShipService shipService;

    @Autowired
    public void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "/rest/ships")
    public ResponseEntity<List<Ship>> getShips(@RequestParam(value = "name") Optional<String> name,
                                               @RequestParam(value = "planet") Optional<String> planet,
                                               @RequestParam(value = "shipType") Optional<ShipType> shipType,
                                               @RequestParam(value = "after") Optional<Long> after,
                                               @RequestParam(value = "before")Optional<Long> before,
                                               @RequestParam(value = "isUsed") Optional<Boolean> isUsed,
                                               @RequestParam(value = "minSpeed") Optional<Double> minSpeed,
                                               @RequestParam(value = "maxSpeed") Optional<Double> maxSpeed,
                                               @RequestParam(value = "minCrewSize") Optional<Integer> minCrewSize,
                                               @RequestParam(value = "maxCrewSize") Optional<Integer> maxCrewSize,
                                               @RequestParam(value = "minRating") Optional<Double> minRating,
                                               @RequestParam(value = "maxRating") Optional<Double> maxRating,
                                               @RequestParam(value = "order") Optional<ShipOrder> order,
                                               @RequestParam(value = "pageNumber") Optional<Integer> pageNumber,
                                               @RequestParam(value = "pageSize") Optional<Integer> pageSize
    ) {
//        Поиск по полям name и planet происходить по частичному соответствию. Например, если в БД есть корабль с именем «Левиафан»,
//        а параметр name задан как «иа» - такой корабль должен отображаться в результатах (Левиафан).
//        pageNumber – параметр, который отвечает за номер отображаемой страницы при использовании пейджинга. Нумерация начинается с нуля
//        pageSize – параметр, который отвечает за количество результатов на одной странице при пейджинге
        Pageable pageable = PageRequest.of(pageNumber.orElse(0), pageSize.orElse(3), Sort.by(order.orElse(ShipOrder.ID).getFieldName()));
        Specification<Ship> specification = getSpecification(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        List<Ship> all = shipService.getAll(specification, pageable);
        ResponseEntity<List<Ship>> responseEntity = new ResponseEntity<>(all, HttpStatus.OK);
        return responseEntity;
    }

    private Specification<Ship> getSpecification(@RequestParam("name") Optional<String> name,
                                                 @RequestParam("planet") Optional<String> planet,
                                                 @RequestParam("shipType") Optional<ShipType> shipType,
                                                 @RequestParam("after") Optional<Long> after,
                                                 @RequestParam("before") Optional<Long> before,
                                                 @RequestParam("isUsed") Optional<Boolean> isUsed,
                                                 @RequestParam("minSpeed") Optional<Double> minSpeed,
                                                 @RequestParam("maxSpeed") Optional<Double> maxSpeed,
                                                 @RequestParam("minCrewSize") Optional<Integer> minCrewSize,
                                                 @RequestParam("maxCrewSize") Optional<Integer> maxCrewSize,
                                                 @RequestParam("minRating") Optional<Double> minRating,
                                                 @RequestParam("maxRating") Optional<Double> maxRating) {
        Specification<Ship> specification = Specification.where(shipService.filterByName(name.orElse(null)))
                                                .and(shipService.filterByPlanet(planet.orElse(null)))
                                                .and(shipService.filterByShipType(shipType.orElse(null)))
                                                .and(shipService.filterByDate(after.orElse(null), before.orElse(null)))
                                                .and(shipService.filterByUsage(isUsed.orElse(null)))
                                                .and(shipService.filterBySpeed(minSpeed.orElse(null),maxSpeed.orElse(null)))
                                                .and(shipService.filterByCrewSize(minCrewSize.orElse(null), maxCrewSize.orElse(null)))
                                                .and(shipService.filterByRating(minRating.orElse(null),maxRating.orElse(null)));
        return specification;
    }

    @GetMapping(value = "/rest/ships/count")
    public Integer getCount(@RequestParam(value = "name") Optional<String> name,
                            @RequestParam(value = "planet") Optional<String> planet,
                            @RequestParam(value = "shipType") Optional<ShipType> shipType,
                            @RequestParam(value = "after") Optional<Long> after,
                            @RequestParam(value = "before")Optional<Long> before,
                            @RequestParam(value = "isUsed") Optional<Boolean> isUsed,
                            @RequestParam(value = "minSpeed") Optional<Double> minSpeed,
                            @RequestParam(value = "maxSpeed") Optional<Double> maxSpeed,
                            @RequestParam(value = "minCrewSize") Optional<Integer> minCrewSize,
                            @RequestParam(value = "maxCrewSize") Optional<Integer> maxCrewSize,
                            @RequestParam(value = "minRating") Optional<Double> minRating,
                            @RequestParam(value = "maxRating") Optional<Double> maxRating
    ) {
        Specification<Ship> specification = getSpecification(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        List<Ship> list = shipService.getAll(specification);
        return list.size();
    }


    @PostMapping(value = "/rest/ships")
    public ResponseEntity<Ship> createShip(
            @RequestBody Ship ship,
            @RequestParam(value = "isUsed") Optional<Boolean> isUsed  // --optional, default=false
    ) {
//        Мы не можем создать корабль, если:
//        - указаны не все параметры из Data Params (кроме isUsed);
//        - длина значения параметра “name” или “planet” превышает размер соответствующего поля в БД (50 символов);
//        - значение параметра “name” или “planet” пустая строка;
//        - скорость или размер команды находятся вне заданных пределов;
//        - “prodDate”:[Long] < 0;
//        - год производства находятся вне заданных пределов.
//        В случае всего вышеперечисленного необходимо ответить ошибкой с кодом 400.
        if(ship == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if(ship.getName() == null || ship.getName().isEmpty() || ship.getName().length() > 50)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if(ship.getPlanet() == null || ship.getPlanet().isEmpty() || ship.getPlanet().length() > 50)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (ship.getShipType() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if(ship.getProdDate() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        else if(ship.getProdDate().getTime() < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if(ship.getSpeed() == null || ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if(ship.getCrewSize() == null || ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if(ship.getUsed() == null){
            ship.setUsed(isUsed.orElse(false));
        }
        ship.updateRating();
        return new ResponseEntity<>(shipService.save(ship), HttpStatus.OK);
    }

    @DeleteMapping(value = "/rest/ships/{id}")
    public ResponseEntity<Ship> deleteShip(@PathVariable long id) {
//        Если корабль не найден в БД, необходимо ответить ошибкой с кодом 404.
//        Если значение id не валидное, необходимо ответить ошибкой с кодом 400.
        if(id <= 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Optional<Ship> ship = shipService.getShip(id);
        if (ship.isPresent())
            return new ResponseEntity<>(shipService.delete(id), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/rest/ships/{id}")
    public ResponseEntity<Ship> updateShip(
            @RequestBody Ship ship,
            @PathVariable long id) {
        //Обновлять нужно только те поля, которые не null.
        //Если корабль не найден в БД, необходимо ответить ошибкой с кодом 404.
        //Если значение id не валидное, необходимо ответить ошибкой с кодом 400.
        if(ship == null)
            return new ResponseEntity<>(HttpStatus.OK);
        if (id <= 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Optional<Ship> fromDB = shipService.getShip(id);
        if (!fromDB.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Ship shipFromDB = fromDB.get();
        if(ship.getName() != null) {
            if (ship.getName().isEmpty() || ship.getName().length() > 50)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            shipFromDB.setName(ship.getName());
        }
        if(ship.getPlanet() != null) {
            if (ship.getPlanet().isEmpty() || ship.getPlanet().length() > 50)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            shipFromDB.setPlanet(ship.getPlanet());
        }
        if (ship.getShipType() != null) {
            shipFromDB.setShipType(ship.getShipType());
        }
        if(ship.getProdDate() != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(ship.getProdDate());
            if(calendar.get(Calendar.YEAR)< 2800 || calendar.get(Calendar.YEAR) >3019)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            shipFromDB.setProdDate(ship.getProdDate());
        }
        if(ship.getUsed() != null) {
            shipFromDB.setUsed(ship.getUsed());
        }
        if(ship.getSpeed() != null) {
            if (ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            shipFromDB.setSpeed(ship.getSpeed());
        }
        if(ship.getCrewSize() != null) {
            if (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            shipFromDB.setCrewSize(ship.getCrewSize());
        }
        shipFromDB.updateRating();
        return new ResponseEntity<>(shipService.update(shipFromDB), HttpStatus.OK);
    }


    @GetMapping(value = "/rest/ships/{id}")
    public ResponseEntity<Ship> getShip(@PathVariable long id){
//        Если корабль не найден в БД, необходимо ответить ошибкой с кодом 404 NOT_FOUND
//        Если значение id не валидное, необходимо ответить ошибкой с кодом 400 BAD_REQUEST
//        Optional<Ship> ship = shipService.getShip(id);
//        return new ResponseEntity<>(ship.get(), HttpStatus.OK);
        if(id <= 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Optional<Ship> ship = shipService.getShip(id);
        if (ship.isPresent())
            return new ResponseEntity<>(ship.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
