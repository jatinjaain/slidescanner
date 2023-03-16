package com.jatin.slidescanner.controllers;

import com.jatin.slidescanner.models.UserState;
import com.jatin.slidescanner.services.ScanningService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("http://localhost:3000")
public class ScanningController {

//    LoggerFactory loggerFactory = new Logger

    @Autowired
    private ScanningService scanningService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/addMove")
    public void addMove(@Payload String move){
        if(move.equals("getState")){
            updateUserState();
            return;
        }
        System.out.println("received"+move);
        scanningService.addInput(move, this);
        return;
    }

    public UserState updateUserState(){
        System.out.print("sending data ");
        UserState userState = scanningService.getUserState();
        System.out.println(userState);
        simpMessagingTemplate.convertAndSend("/userState/update", userState);
        return userState;
    }

    @MessageMapping("/getOffset")
    @SendTo("/userState/offset")
    public Integer getCurrPosition(){
        System.out.println("client wants curr position");
        Integer[] offset = scanningService.getOffset();
        return offset[0]*60+offset[1];
    }





}
