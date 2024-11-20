package com.pxr.golf.models;

public class Hole {
    private final String id;
    private final int number;
    private final int par;
    private int score = 0;
    private String note = "";

    public Hole(String id, int number, int par) {
        this.id = id;
        this.number = number;
        this.par = par;
    }

    public Hole(String id, int number, int par, int score, String note) {
        this.id = id;
        this.number = number;
        this.par = par;
        this.score = score;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public int getPar() {
        return par;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
