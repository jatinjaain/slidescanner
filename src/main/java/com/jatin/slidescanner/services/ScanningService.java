package com.jatin.slidescanner.services;


import com.jatin.slidescanner.controllers.ScanningController;
import com.jatin.slidescanner.enums.MachineState;
import com.jatin.slidescanner.models.UserState;

public interface ScanningService {

    public Integer[] getOffset();

    public void addInput(String direction, ScanningController controller);

    public void updateOffset();

    public UserState getUserState();
    public void setMachineState(MachineState machineState);

    public void sendState();
}
