package com.test.pushnotification.contoller;

import com.test.pushnotification.events.UserEventTypes;
import com.test.pushnotification.model.User;
import com.test.pushnotification.request.message.UserMessageRequest;
import com.test.pushnotification.response.Response;
import com.test.pushnotification.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
@Tag(name = "Users Endpoints")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "/connect/{username}")
    @Operation(summary = "Create new user and establish a connection")
    //@PostMapping(value = "/connect")
//    public SseEmitter createNewUser(@RequestBody UserLoginRequest request) {
    public Object createNewUser(@PathVariable String username) {
        return userService.addUser(username).getSseEmitter();
    }

    @GetMapping(value = "/get/{username}")
    @Operation(summary = "Get user data")
    public Response getUser(@PathVariable("username") String username) {
        return userService.getUser(username);
    }

    @GetMapping(value = "/get/all")
    @Operation(summary = "Get all user data")
    public Collection<User> getAllUser() {
        return userService.getAllUser();
    }

    @PostMapping(value = "/message")
    @Operation(summary = "Send a message")
    public ResponseEntity<String> createNewMessage(@RequestBody UserMessageRequest request) {
        try {
            userService.newMessage(request);
            return ResponseEntity.ok("Message sent successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete a user and close the connection")
    public ResponseEntity<Integer> deleteUser(@RequestParam("username") String username) {
        userService.delete(username);
        return ResponseEntity.ok(200);
    }

    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe an event")
    public ResponseEntity<Integer> subscribeUser(@RequestParam("username") String username, @RequestParam("event") UserEventTypes event) {
        userService.subscribe(username, event);
        return ResponseEntity.ok(200);
    }

    @PostMapping("/unsubscribe")
    @Operation(summary = "Unsubscribe an event")
    public ResponseEntity<Integer> unsubscribeUser(@RequestParam("username") String username, @RequestParam("event") UserEventTypes event) {
        userService.unsubscribe(username, event);
        return ResponseEntity.ok(200);
    }

    @PostMapping("/unsubscribe/all")
    @Operation(summary = "Unsubscribe an event")
    public ResponseEntity<Integer> unsubscribeUserFromAllEvents(@RequestParam("username") String username) {
        userService.unsubscribeFromAllEvents(username);
        return ResponseEntity.ok(200);
    }
}
