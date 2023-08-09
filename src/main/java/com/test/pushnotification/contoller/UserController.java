package com.test.pushnotification.contoller;

import com.test.pushnotification.model.User;
import com.test.pushnotification.publisher.Events;
import com.test.pushnotification.request.EventMessageRequest;
import com.test.pushnotification.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Set;

@RestController
@CrossOrigin("*")
@Tag(name = "User Events")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "/user/{username}" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Create new user and establish a connection")
    public SseEmitter createNewUser(@PathVariable("username") String username){
        System.out.println("new joiner:"+username);
        User user = new User(username);
        return userService.addUser(user)? user.getSseEmitter() : null;
    }

    @PostMapping("/message")
    @Operation(summary = "Send a message")
    public ResponseEntity<String> createNewMessage(@RequestBody EventMessageRequest request){

        System.out.println(request.getFrom()+": "+request.getMessage());
        return ResponseEntity.ok(userService.newMessage(request));
    }

    @PostMapping("/delete")
    @Operation(summary = "Delete a user and close the connection")
    public ResponseEntity<String> deleteUser(@RequestParam("username") String username){
        return ResponseEntity.ok(userService.delete(username));
    }


    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe an event")
    public ResponseEntity<String> subscribeUser(@RequestParam("username") String username, @RequestParam("events")Set<Events> events){
        return ResponseEntity.ok(userService.subscribe(username,events));
    }

    @PostMapping("/unsubscribe")
    @Operation(summary = "Unsubscribe an event")
    public ResponseEntity<String> unsubscribeUser(@RequestParam("username") String username, @RequestParam("events")Set<Events> events){
        return ResponseEntity.ok(userService.unsubscribe(username,events));
    }

    @PostMapping("/unsubscribe-all")
    @Operation(summary = "Unsubscribe an event")
    public ResponseEntity<String> unsubscribeUserFromAllEvents(@RequestParam("username") String username){
        return ResponseEntity.ok(userService.unsubscribeFromAllEvents(username));
    }
}
