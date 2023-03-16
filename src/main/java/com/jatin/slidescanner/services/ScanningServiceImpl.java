package com.jatin.slidescanner.services;

import com.jatin.slidescanner.controllers.ScanningController;
import com.jatin.slidescanner.enums.MachineState;
import com.jatin.slidescanner.models.UserState;
import com.jatin.slidescanner.utils.Capturer;
import com.jatin.slidescanner.utils.Focuser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

//import static com.jatin.slidescanner.enums.MachineState.*;

@Service
public class ScanningServiceImpl implements ScanningService {

    Integer rows = 20;
    Integer columns = 60;

    ScanningController scanningController;

    MachineState machineState = MachineState.IDLE;
    Integer[] initialPosition = new Integer[]{0,0};
    Integer[] offset = new Integer[]{0,0};
    UserState userState = new UserState();

    ArrayDeque<Integer[]> inputQueue = new ArrayDeque<>();
    Semaphore binary = new Semaphore(1);


    public Integer[] getOffset(){
        return this.offset;
    }

    @Override
    public void addInput(String direction, ScanningController controller){
        this.scanningController=controller;

        if(direction.equals("")){
            sendState();
            return;
        }

        if(direction.equals("right")){
            inputQueue.add(new Integer[]{0, 1});
        }
        else if(direction.equals("left")){
            inputQueue.add(new Integer[]{0, -1});
        }
        else if(direction.equals("down")){
            inputQueue.add(new Integer[]{1, 0});
        }
        else if(direction.equals("up")){
            inputQueue.add(new Integer[]{-1, 0});
        }

        System.out.println("added to input queue and size "+ inputQueue.size());
        if(machineState == MachineState.IDLE)
            updateOffset();

    }

    public void updateOffset() {
        System.out.println("updating offset");
        while(inputQueue.size()>0){

            Integer[] currInput = inputQueue.removeFirst();

            offset[0] += currInput[0];
            offset[1] += currInput[1];
            if(offset[0]<0){
                offset[0]=rows-1;
            }
            else if(offset[0]==rows){
                offset[0]=0;
            }
            if(offset[1]<0){
                offset[0] = offset[0]==0 ? rows-1 : offset[0]-1;
                offset[1] = columns-1;
            }
            else if(offset[1]==columns){
                offset[0]= offset[0]==rows-1 ? 0 : offset[0]+1;
                offset[1] = 0;
            }
        }
//        System.out.println("offset "+ offset[0] +" "+offset[1]);


        reFocus();

    }


    void reFocus() {

        Integer[] positionForFocus = new Integer[2];
        positionForFocus[0] = initialPosition[0]+offset[0];
        positionForFocus[1] = initialPosition[1]+offset[1];

        Focuser focuser = new Focuser(userState, positionForFocus, binary, machineState, this);
        focuser.start();

        imageCapture();

    }


    void imageCapture() {

        Integer[] positionForCapture = new Integer[2];
        positionForCapture[0] = initialPosition[0]+offset[0];
        positionForCapture[1] = initialPosition[1]+offset[1];

        Capturer capturer = new Capturer(userState, positionForCapture, inputQueue, binary, machineState, this);
        capturer.start();

    }

    public UserState getUserState(){
        return userState;
    }

    public void setMachineState(MachineState machineState){
        this.machineState=machineState;
    }

    public void sendState(){
        scanningController.updateUserState();
    }


    // trial to test web socket
//    public void addInput(String direction, ScanningController controller){
//        scanningController = controller;
//        userState.setCurrFocus(new Integer[]{2,4});
//        sendState();
//        try {
//            Thread.sleep(3000); // focus
//        }
//        catch (Exception e){
//
//        }
//        userState.setCurrFocus(new Integer[]{});
//        userState.setCurrCapture(new Integer[]{2,4});
//        sendState();
//        // for testing
//        try {
//            Thread.sleep(2000);
//        }
//        catch (Exception e){
//
//        }
//        userState.setCurrCapture(new Integer[]{});
//        sendState();
//    }

}
