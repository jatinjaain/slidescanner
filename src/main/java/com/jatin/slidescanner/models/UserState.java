package com.jatin.slidescanner.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserState {

    Integer[] currFocus = new Integer[]{};
    Integer[] currCapture = new Integer[]{};
    List<Integer[]> alreadyFocused = new ArrayList<>();
    List<Integer[]> alreadyCaptured = new ArrayList<>();

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
