package com.pxr.golf.utils;

import com.pxr.golf.R;
import com.pxr.golf.models.Course;
import com.pxr.golf.models.Hole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generate {
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
        Random random = new Random();
        int[] pars = {4, 3, 5};
        double[] probabilities = {0.6, 0.2, 0.2};

        List<Integer> parList = new ArrayList<>();
        int totalPar = 0;

        while (parList.size() < n) {
            double rand = random.nextDouble();
            int selectedPar = 0;

            for (int i = 0; i < probabilities.length; i++) {
                if (rand < probabilities[i]) {
                    selectedPar = pars[i];
                    break;
                }
                rand -= probabilities[i];
            }

            if (totalPar + selectedPar <= maxTotalPar) {
                parList.add(selectedPar);
                totalPar += selectedPar;
            }
        }

        while (totalPar > maxTotalPar) {
            for (int i = 0; i < parList.size(); i++) {
                if (parList.get(i) == 5) {
                    parList.set(i, 4);
                    totalPar -= 1;
                    break;
                } else if (parList.get(i) == 4) {
                    parList.set(i, 3);
                    totalPar -= 1;
                    break;
                }
            }
        }

        return parList;
    }
}
