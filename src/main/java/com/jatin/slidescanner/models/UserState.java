package com.jatin.slidescanner.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserState {

    Integer[] currFocus = new Integer[]{}; // this is the point that is undergoing focus
    Integer[] currCapture = new Integer[]{}; // this is the point that is undergoing capture
    List<Integer[]> alreadyFocused = new ArrayList<>(); // contains all the already focused points
    List<Integer[]> alreadyCaptured = new ArrayList<>(); // contains all the already captured points

    public UserState() {
    }

    public UserState(Integer[] currFocus, Integer[] currCapture, List<Integer[]> alreadyFocused, List<Integer[]> alreadyCaptured) {
        this.currFocus = currFocus;
        this.currCapture = currCapture;
        this.alreadyFocused = alreadyFocused;
        this.alreadyCaptured = alreadyCaptured;
    }

    public Integer[] getCurrFocus() {
        return currFocus;
    }

    public void setCurrFocus(Integer[] currFocus) {
        this.currFocus = currFocus;
    }

    public Integer[] getCurrCapture() {
        return currCapture;
    }

    public void setCurrCapture(Integer[] currCapture) {
        this.currCapture = currCapture;
    }

    public List<Integer[]> getAlreadyFocused() {
        return alreadyFocused;
    }

    public void setAlreadyFocused(List<Integer[]> alreadyFocused) {
        this.alreadyFocused = alreadyFocused;
    }

    public List<Integer[]> getAlreadyCaptured() {
        return alreadyCaptured;
    }

    public void setAlreadyCaptured(List<Integer[]> alreadyCaptured) {
        this.alreadyCaptured = alreadyCaptured;
    }

    @Override
    public String toString() {
        return "UserState{" +
                "currFocus=" + Arrays.toString(currFocus) +
                ", currCapture=" + Arrays.toString(currCapture) +
                ", alreadyFocused=" + alreadyFocused +
                ", alreadyCaptured=" + alreadyCaptured +
                '}';
    }
}
