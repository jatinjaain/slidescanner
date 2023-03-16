package com.jatin.slidescanner.utils;

import com.jatin.slidescanner.controllers.ScanningController;
import com.jatin.slidescanner.enums.MachineState;
import com.jatin.slidescanner.models.UserState;
import com.jatin.slidescanner.services.ScanningService;
import com.jatin.slidescanner.services.ScanningServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Focuser extends Thread{
    int delay = 3000;
    Integer[] positionForFocus = null;
    List<Integer[]> alreadyFocused = new ArrayList<>();
    Semaphore binary;
    MachineState state;
    UserState userState;
    ScanningService scanningService;
    public Focuser(UserState userState, Integer[] positionForFocus, Semaphore binary, MachineState state, ScanningService scanningService){
        this.userState=userState;
        this.alreadyFocused=userState.getAlreadyFocused();
        this.positionForFocus=positionForFocus;
        this.binary = binary;
        this.state=state;
        this.scanningService=scanningService;
    }

    @Override
    public void run(){
        try {
            binary.acquire();

            userState.setCurrFocus(positionForFocus);
            scanningService.sendState();

            boolean isFocused = false;
            for(Integer[] focusedPair: userState.getAlreadyFocused()){
                if(focusedPair[0]==positionForFocus[0] && focusedPair[1]==positionForFocus[1]){
                    isFocused = true;
                }
            }
            boolean isCaptured = false;
            for(Integer[] capturedPair: userState.getAlreadyCaptured()){
                if(capturedPair[0]==positionForFocus[0] && capturedPair[1]==positionForFocus[1]){
                    isCaptured = true;
                }
            }
            if(isFocused==true || isCaptured==true){
                userState.setCurrFocus(new Integer[]{});
                scanningService.sendState();
                binary.release();
                return;
            }

            scanningService.setMachineState(MachineState.FOCUSING);
            System.out.println("Focus start");
            Thread.sleep(delay);    // this is the actual process which takes 3 seconds
            System.out.println("Focus end");

            alreadyFocused.add(positionForFocus);
            userState.setCurrFocus(new Integer[]{});
            scanningService.sendState();

            binary.release();
        }
        catch (Exception e){
            System.out.println("error "+e);
        }
    }

}
