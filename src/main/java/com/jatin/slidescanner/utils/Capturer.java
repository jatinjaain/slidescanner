package com.jatin.slidescanner.utils;

import com.jatin.slidescanner.enums.MachineState;
import com.jatin.slidescanner.models.UserState;
import com.jatin.slidescanner.services.ScanningService;

import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Capturer extends Thread{
    int delay = 2000;
    List<Integer[]> alreadyCaptured;
    ArrayDeque<Integer[]> inputQueue;
    Integer[] positionForCapture;

//    Integer[] currFocus;
    Semaphore binary;
    MachineState state;
    UserState userState;

    ScanningService scanningService;
    public Capturer(UserState userState, Integer[] positionForCapture, ArrayDeque<Integer[]> inputQueue, Semaphore binary, MachineState state, ScanningService scanningService){
        this.userState=userState;
        this.positionForCapture=positionForCapture;
        this.alreadyCaptured=userState.getAlreadyCaptured();
        this.inputQueue = inputQueue;
//        this.currFocus=userState.getCurrFocus();
        this.binary = binary;
        this.state=state;
        this.scanningService=scanningService;
    }

    @Override
    public void run(){
        try {
            binary.acquire();
            if(inputQueue.size()!=0){
                System.out.println(" focus is done but not capturing because input queue is having this much moves " + inputQueue.size());
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


            System.out.println("capture start");
            Thread.sleep(delay);    // this is the actual process which takes 2 seconds
            System.out.println("capture end");

            alreadyCaptured.add(positionForCapture);
            userState.setCurrCapture(new Integer[]{});
            scanningService.sendState();


            if(inputQueue.size()!=0){
                System.out.println(" capturing is done but input queue is having this much moves " + inputQueue.size());
                binary.release();
                scanningService.setMachineState(MachineState.IDLE);
                scanningService.updateOffset();
                return;
            }
            scanningService.setMachineState(MachineState.IDLE);

            binary.release();
        }
        catch (Exception e){
            System.out.println("error "+e);
        }
    }
}
