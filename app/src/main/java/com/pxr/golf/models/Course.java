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
    private String hid;

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

    public Course(String id, String name, int image, String hid, List<Hole> holes) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.holes = holes;
        this.hid = hid;
        this.holeCount = holes.size();
    }

    public Course(String id, String name, int image, String hid) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.hid = hid;
        this.holeCount = 18;
    }

    public String getId() {
        return id;
    }

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
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
}
