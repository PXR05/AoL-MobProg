package com.pxr.golf.models;

import java.time.LocalDate;
import java.util.List;

public class Course {
    private final String id;
    private final String name;
    private final int image;
    private final int holeCount;
    private List<Hole> holes;
    private LocalDate date;

    public Course(String id, String name, int image, int holeCount) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.holeCount = holeCount;
    }

    public Course(String id, String name, int image, List<Hole> holes) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.holes = holes;
        this.holeCount = holes.size();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getHoleCount() {
        return holeCount;
    }

    public List<Hole> getHoles() {
        return holes;
    }

    public void setHoles(List<Hole> holes) {
        this.holes = holes;
    }
}
