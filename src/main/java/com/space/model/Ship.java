package com.space.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.persistence.*;

@Entity
@Table(name = "ship")
public class Ship {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;             //ID корабля

    @Column(name = "name")
    private String name;         //Название корабля (до 50 знаков включительно)

    @Column(name = "planet")
    private String planet;       //Планета пребывания (до 50 знаков включительно)

    @Column(name = "shipType")
    @Enumerated(value = EnumType.STRING)
    private ShipType shipType;   //Тип корабля


    @Column(name = "prodDate")
    @Temporal(value = TemporalType.DATE)
    private Date prodDate;       //Дата выпуска. Диапазон значений года 2800..3019 включительно

    @Column(name = "isUsed")
    private Boolean isUsed;      //Использованный / новый

    @Column(name = "speed")
    private Double speed;        //Максимальная скорость корабля. Диапазон значений 0,01..0,99 включительно. Используй математическое округление до сотых.

    @Column(name = "crewSize")
    private Integer crewSize;    //Количество членов экипажа. Диапазон значений 1..9999 включительно.

    @Column(name = "rating")
    private Double rating;       //Рейтинг корабля. Используй математическое округление до сотых.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanet() {
        return planet;
    }

    public void setPlanet(String planet) {
        this.planet = planet;
    }

    public ShipType getShipType() {
        return shipType;
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    public Date getProdDate() {
        return prodDate;
    }

    public void setProdDate(Date prodDate) {
        this.prodDate = prodDate;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getCrewSize() {
        return crewSize;
    }

    public void setCrewSize(Integer crewSize) {
        this.crewSize = crewSize;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(double rating){
        this.rating = rating;
    }

    public void updateRating() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(prodDate);
        int year = calendar.get(Calendar.YEAR);
        double k = isUsed ? 0.5d : 1d;
        rating = 80d * speed *k / (3019 - year + 1);
        BigDecimal temp = new BigDecimal(rating);
        rating = temp.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

//    public boolean isEmpty(){
//        if (name == null || planet == null || shipType == null ||
//                prodDate == null || isUsed == null || speed == null || crewSize == null)
//            return true;
//        else
//            return false;
//    }

    @Override
    public String toString() {
        return id + " " + name + " " + planet + " " + shipType + " " + prodDate + " " + isUsed + " " + speed + " " + crewSize + " " + rating;
    }
}
