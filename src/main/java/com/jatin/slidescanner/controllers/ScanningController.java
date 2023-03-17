package com.jatin.slidescanner.controllers;

import com.jatin.slidescanner.models.UserState;
import com.jatin.slidescanner.services.ScanningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("http://localhost:3000")
public class ScanningController {

    Logger logger = LoggerFactory.getLogger(ScanningController.class);

    @Autowired
    private ScanningService scanningService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping("/addMove")
    public void addMove(@Payload String move){
        try{
            if(move.equals("getState")){    // this is used to get the UserState when the connection is re established
                logger.info("Client wants current user state");
                sendUserState();
                return;
            }
            scanningService.addInput(move, this);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public UserState sendUserState(){
        try {
            logger.info("Sending userState to client because userState changed");
            UserState userState = scanningService.getUserState();
            simpMessagingTemplate.convertAndSend("/userState/update", userState);
            return userState;
        }
        catch (Exception e){
            e.printStackTrace();
            return new UserState();
        }
    }

    @MessageMapping("/getOffset")
    @SendTo("/userState/offset")
    public Integer getCurrPosition(){
        // used to get current location from backend when the connection is re established
        logger.info("Client wants current position");
        return scanningService.getOffset();
    }





}
