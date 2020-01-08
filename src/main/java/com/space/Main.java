package com.space;

import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Ship> ships = new ArrayList<>();
        //ships.add(new ShipInfoTest(4L, "F-302 Mongoose", "Neptune", ShipType.MILITARY, 32872204329667L, false, 0.24, 2170, 2.13));
        // allShips.add(new ShipInfoTest(5L, "Excalibur", "Mercury", ShipType.MILITARY, 32872204329667L, false, 0.64, 128, 5.69));
        //allShips.add(new ShipInfoTest(12L, "Hunter IV", "Jupiter", ShipType.MILITARY, 32840668329670L, false, 0.71, 4379, 5.68));
        //allShips.add(new ShipInfoTest(15L, "Mark IX Hawk", "Jupiter", ShipType.MILITARY, 32619916329672L, true, 0.58, 927, 1.36));
        //allShips.add(new ShipInfoTest(17L, "Amaterasu", "Saturn", ShipType.MILITARY, 32746060329672L, true, 0.88, 1517, 2.71));

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(32872204329667L));
        System.out.println("F-302 Mongoose " + calendar.getTime());
        calendar.setTime(new Date(32872204329667L));
        System.out.println("Excalibur " + calendar.getTime());
        calendar.setTime(new Date(32840668329670L));
        System.out.println("Hunter IV " + calendar.getTime());
        calendar.setTime(new Date(32619916329672L));
        System.out.println("Mark IX Hawk " + calendar.getTime());
        calendar.setTime(new Date(32746060329672L));
        System.out.println("Amaterasu " + calendar.getTime());
        calendar.setTime(new Date(32503672800000L));
        System.out.println("after " + calendar.getTime());
        calendar.setTime(new Date(32850741600000L));
        System.out.println("before " + calendar.getTime());

        System.out.println(new Date(32872204329667L));
        System.out.println(new Date(32872204329667L));
        System.out.println(new Date(32840668329670L));
        System.out.println(new Date(32619916329672L));
        System.out.println(new Date(32746060329672L));
        System.out.println(new Date(32503672800000L));
        System.out.println(new Date(32850741600000L));


//        long l1 = 32503672800000L;
//        long l2 = 32850741600000L;
//        Calendar calendar = new GregorianCalendar();
//        calendar.setTime(new Date(l1));
//        System.out.println(calendar.get(Calendar.YEAR));
//        calendar.setTime(new Date(l2));
//        System.out.println(calendar.getTime());
//        long l3 = 32850741600000L + 31536000000L -18000001L;
//        calendar.setTime(new Date(l3));
//        System.out.println(calendar.getTime());

    }


}
