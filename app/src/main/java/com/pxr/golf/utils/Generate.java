package com.pxr.golf.utils;

import com.pxr.golf.R;
import com.pxr.golf.models.Course;
import com.pxr.golf.models.Hole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generate {
    private static final Random random = new Random();

    public static List<Course> courses(int n) {
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            courses.add(new Course(String.valueOf(i), "Course " + i, R.drawable._0, holes(18, 72)));
        }
        return courses;
    }

    public static Course course(String id) {
        return new Course(id, "Course " + id, R.drawable._0, holes(18, 72));
    }

    public static List<Hole> holes(int n, int totalPar) {
        List<Integer> pars = pars(n, totalPar);
        List<Hole> holes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            holes.add(new Hole(String.valueOf(i), i + 1, pars.get(i)));
        }
        return holes;
    }

    public static List<Integer> pars(int n, int maxTotalPar) {
        List<Integer> parList = new ArrayList<>();
        int remainingPar = maxTotalPar;

        for (int i = 0; i < n; i++) {
            parList.add(4);
        }
        remainingPar -= (4 * n);

        if (remainingPar > 0) {
            for (int i = 0; i < remainingPar && i < n / 3; i++) {
                parList.set(i * 3, 5);
            }
        } else if (remainingPar < 0) {
            for (int i = 0; i < Math.abs(remainingPar) && i < n / 3; i++) {
                parList.set(i * 3 + 1, 3);
            }
        }

        return parList;
    }
}
