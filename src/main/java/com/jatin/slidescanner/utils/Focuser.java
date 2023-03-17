package com.jatin.slidescanner.utils;

import com.jatin.slidescanner.enums.MachineState;
import com.jatin.slidescanner.models.UserState;
import com.jatin.slidescanner.services.ScanningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Focuser extends Thread{
    Logger logger = LoggerFactory.getLogger(Focuser.class);
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
            logger.info("Focus start");
            Thread.sleep(delay);    // this is the actual process which takes 3 seconds
            logger.info("Focus end");

            alreadyFocused.add(positionForFocus);
            userState.setCurrFocus(new Integer[]{});
            scanningService.sendState();

            binary.release();
        }
        catch (InterruptedException e){
            logger.error("problem while calling Thread.sleep(), error"+e);
        }
        catch (Exception e){
            logger.error("Exception: "+e);
        }
    }

}
