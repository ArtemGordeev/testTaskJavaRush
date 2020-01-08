package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
@Transactional
public class ShipService {

    @Autowired
    private ShipRepository repository;

    public List<Ship> getAll(Specification<Ship> specification, Pageable pageable) {
        Page<Ship> all = repository.findAll(specification, pageable);
        return all.getContent();
    }

    public List<Ship> getAll(Specification<Ship> specification) {
        List<Ship> all = repository.findAll(specification);
        return all;
    }

    public Ship save(Ship ship) {
        return repository.save(ship);
    }

    public Ship delete(long id) {
        Ship ship = repository.findById(id).get();
        repository.deleteById(id);
        return ship;
    }

    public Ship update(Ship ship) {
        return repository.save(ship);
    }

    public Optional<Ship> getShip(long id) {
        return repository.findById(id);
    }

    public Specification<Ship> filterByName(String name) {
        return (Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if(name == null)
                return null;
            else
                return cb.like(root.get("name"), "%" + name + "%");
        };
    }

    public Specification<Ship> filterByPlanet(String planet) {
        return (Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (planet == null)
                return null;
            else
                return cb.like(root.get("planet"), "%" + planet + "%");
        };
    }

    public Specification<Ship> filterByShipType(ShipType shipType) {
        return (Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if(shipType == null)
                return null;
            else
                return cb.equal(root.get("shipType"), shipType);
        };
    }

    public Specification<Ship> filterByDate(Long after, Long before) {
        return (Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder cb)  -> {
            if (after == null && before == null)
                return null;
            if (after == null) {
                Date beforeDate = new  Date(before - 31536000000L);
                return cb.lessThanOrEqualTo(root.get("prodDate"), beforeDate);
            }
            if (before == null) {
                Date afterDate = new  Date(after);
                return cb.greaterThanOrEqualTo(root.get("prodDate"), afterDate);
            }
            Date afterDate = new Date(after);
            Date beforeDate = new Date(before - 31536000000L);
            return cb.between(root.get("prodDate"), afterDate, beforeDate);
        };
    }

    public Specification<Ship> filterByUsage(Boolean isUsed) {
        return (Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder cb)  -> {
            if (isUsed == null)
                return null;
            if (isUsed)
                return cb.isTrue(root.get("isUsed"));
            else
                return cb.isFalse(root.get("isUsed"));
        };
    }

    public Specification<Ship> filterBySpeed(Double min, Double max) {
        return (Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder cb)  -> {
            if (min == null && max == null)
                return null;
            if (min == null)
                return cb.lessThanOrEqualTo(root.get("speed"), max);
            if (max == null)
                return cb.greaterThanOrEqualTo(root.get("speed"), min);
            return cb.between(root.get("speed"), min, max);
        };
    }

    public Specification<Ship> filterByCrewSize(Integer min, Integer max) {
        return (Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder cb)  -> {
            if (min == null && max == null)
                return null;
            if (min == null)
                return cb.lessThanOrEqualTo(root.get("crewSize"), max);
            if (max == null)
                return cb.greaterThanOrEqualTo(root.get("crewSize"), min);
            return cb.between(root.get("crewSize"), min, max);
        };
    }

    public Specification<Ship> filterByRating(Double min, Double max) {
        return (Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder cb)  -> {
            if (min == null && max == null)
                return null;
            if (min == null)
                return cb.lessThanOrEqualTo(root.get("rating"), max);
            if (max == null)
                return cb.greaterThanOrEqualTo(root.get("rating"), min);
            return cb.between(root.get("rating"), min, max);
        };
    }
}
