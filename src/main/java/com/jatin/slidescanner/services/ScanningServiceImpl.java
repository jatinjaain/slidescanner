package com.jatin.slidescanner.services;

import com.jatin.slidescanner.controllers.ScanningController;
import com.jatin.slidescanner.enums.MachineState;
import com.jatin.slidescanner.models.UserState;
import com.jatin.slidescanner.utils.Capturer;
import com.jatin.slidescanner.utils.Focuser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.concurrent.Semaphore;


@Service
public class ScanningServiceImpl implements ScanningService {

    Logger logger = LoggerFactory.getLogger(ScanningServiceImpl.class);

    Integer rows = 20;
    Integer columns = 60;

    ScanningController scanningController;

    MachineState machineState = MachineState.IDLE;
    Integer[] initialPosition = new Integer[]{rows/2,columns/2}; // initial position is 10,30
    Integer[] offset = new Integer[]{0,0}; // current position

    UserState userState = new UserState();
    ArrayDeque<Integer[]> inputQueue = new ArrayDeque<>();
    Semaphore binary = new Semaphore(1);


    public Integer getOffset(){
        // used to get current location from backend when the connection is first established
        return (offset[0]+initialPosition[0])*60+(offset[1]+initialPosition[1]);
    }


    @Override
    public void addInput(String direction, ScanningController controller){
        // takes input and the controller object and adds the direction to the input queue

        this.scanningController=controller;


        logger.info("Received "+direction);

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

        if(machineState == MachineState.IDLE)
            updateOffset();

    }

    public void updateOffset() {

        logger.info("Updating offset");

        while(inputQueue.size()>0){

            Integer[] currInput = inputQueue.removeFirst();

            offset[0] += currInput[0];
            offset[1] += currInput[1];

            // below is the code to handle the loop case,
            // i.e. going to last row when up arrow key is pressed while on the first row and so on
            if(offset[0]+initialPosition[0]<0){
                offset[0]=rows-1-initialPosition[0];
            }
            else if(offset[0]+initialPosition[0]==rows){
                offset[0]=-1*initialPosition[0];
            }
            if(offset[1]+initialPosition[1]<0){
                offset[0] = offset[0]+initialPosition[0]==0 ? rows-1-initialPosition[0] : offset[0]-1;
                offset[1] = columns-1-initialPosition[1];
            }
            else if(offset[1]+initialPosition[1]==columns){
                offset[0]= offset[0]+initialPosition[0]==rows-1 ? -1*initialPosition[0] : offset[0]+1;
                offset[1] = -1*initialPosition[1];
            }
        }

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
        // this is used to change machine state of service layer from Focuser util or Capturer util
        this.machineState=machineState;
    }

    public void sendState(){
        // this calls the send user state method of the controller layer, to send the current user state to the client
        // this is used by the Focuser util or Capturer util to send user state after every change in user state
        scanningController.sendUserState();
    }

}
