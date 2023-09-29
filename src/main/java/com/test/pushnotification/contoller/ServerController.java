package com.test.pushnotification.contoller;

import com.test.pushnotification.events.EventType;
import com.test.pushnotification.events.ServerEventTypes;
import com.test.pushnotification.service.ServerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/server")
@CrossOrigin("*")
@Tag(name = "User Events")
@Slf4j
public class ServerController {

    @Autowired
    ServerService serverService;

    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe an event")
    public ResponseEntity<Integer> subscribeUser(@RequestParam("username") String username, @RequestParam("events") ServerEventTypes event){
        serverService.subscribe(username,event);
        return ResponseEntity.ok(200);
    }

    @PostMapping("/unsubscribe")
    @Operation(summary = "Unsubscribe an event")
    public ResponseEntity<Integer> unsubscribeUser(@RequestParam("username") String username, @RequestParam("events") ServerEventTypes event){
        serverService.unsubscribe(username,event);
        return ResponseEntity.ok(200);
    }
    @PostMapping("/unsubscribe-all")
    @Operation(summary = "Unsubscribe an event")
    public ResponseEntity<Integer> unsubscribeUserFromAllEvents(@RequestParam("username") String username){
        serverService.unsubscribeFromAllEvents(username);
        return ResponseEntity.ok(200);
    }
}
