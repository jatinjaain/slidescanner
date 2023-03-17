package com.jatin.slidescanner.utils;

import com.jatin.slidescanner.enums.MachineState;
import com.jatin.slidescanner.models.UserState;
import com.jatin.slidescanner.services.ScanningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Capturer extends Thread{

    Logger logger = LoggerFactory.getLogger(Capturer.class);
    int delay = 2000;
    List<Integer[]> alreadyCaptured;
    ArrayDeque<Integer[]> inputQueue;
    Integer[] positionForCapture;
    Semaphore binary;
    MachineState state;
    UserState userState;

    ScanningService scanningService;
    public Capturer(UserState userState, Integer[] positionForCapture, ArrayDeque<Integer[]> inputQueue, Semaphore binary, MachineState state, ScanningService scanningService){
        this.userState=userState;
        this.positionForCapture=positionForCapture;
        this.alreadyCaptured=userState.getAlreadyCaptured();
        this.inputQueue = inputQueue;
        this.binary = binary;
        this.state=state;
        this.scanningService=scanningService;
    }

    @Override
    public void run(){
        try {
            binary.acquire();
            if(inputQueue.size()!=0){
                //focus is done but not capturing because input queue is having moves
                binary.release();
                scanningService.updateOffset();
                return;
            }

            boolean isCaptured = false;
            for(Integer[] capturedPair: userState.getAlreadyCaptured()){
                if(capturedPair[0]==positionForCapture[0] && capturedPair[1]==positionForCapture[1]){
                    isCaptured = true;
                }
            }

            if(isCaptured==true){
                binary.release();
                scanningService.setMachineState(MachineState.IDLE);
                return;
            }

            scanningService.setMachineState(MachineState.CAPTURING);

            userState.setCurrCapture(positionForCapture);
            scanningService.sendState();


            logger.info("Capture start");
            Thread.sleep(delay);    // this is the actual process which takes 2 seconds
            logger.info("capture end");

            alreadyCaptured.add(positionForCapture);
            userState.setCurrCapture(new Integer[]{});
            scanningService.sendState();


            if(inputQueue.size()!=0){
                //capturing is done but input queue is having moves
                binary.release();
                scanningService.setMachineState(MachineState.IDLE);
                scanningService.updateOffset();
                return;
            }
            scanningService.setMachineState(MachineState.IDLE);

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
