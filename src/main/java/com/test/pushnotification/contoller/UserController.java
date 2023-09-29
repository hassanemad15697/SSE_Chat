package com.test.pushnotification.contoller;

import com.test.pushnotification.events.UserEventTypes;
import com.test.pushnotification.request.message.UserMessageRequest;
import com.test.pushnotification.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
@Tag(name = "Users Endpoints")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    // keep it GET to be able to test it from browser
    @GetMapping(value = "/connect/{username}" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Create new user and establish a connection")
    public SseEmitter createNewUser(@PathVariable("username") String username) {
        return userService.addUser(username).getSseEmitter();
    }

    @PostMapping("/message")
    @Operation(summary = "Send a message")
    public ResponseEntity<Integer> createNewMessage(@RequestBody UserMessageRequest request){
        userService.newMessage(request);
        return ResponseEntity.ok(200);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete a user and close the connection")
    public ResponseEntity<Integer> deleteUser(@RequestParam("username") String username){
        userService.delete(username);
        return ResponseEntity.ok(200);
    }

    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe an event")
    public ResponseEntity<Integer> subscribeUser(@RequestParam("username") String username, @RequestParam("events") UserEventTypes event){
        userService.subscribe(username,event);
        return ResponseEntity.ok(200);
    }

    @PostMapping("/unsubscribe")
    @Operation(summary = "Unsubscribe an event")
    public ResponseEntity<Integer> unsubscribeUser(@RequestParam("username") String username, @RequestParam("events") UserEventTypes event){
        userService.unsubscribe(username,event);
        return ResponseEntity.ok(200);
    }

    @PostMapping("/unsubscribe-all")
    @Operation(summary = "Unsubscribe an event")
    public ResponseEntity<Integer> unsubscribeUserFromAllEvents(@RequestParam("username") String username){
        userService.unsubscribeFromAllEvents(username);
        return ResponseEntity.ok(200);
    }
}
