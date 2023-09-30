package com.test.pushnotification.contoller;

import com.test.pushnotification.events.GroupEventType;
import com.test.pushnotification.events.UserEventTypes;
import com.test.pushnotification.model.GroupPermissions;
import com.test.pushnotification.request.GroupMemberRequest;
import com.test.pushnotification.request.GroupRequest;
import com.test.pushnotification.request.message.GroupMessageRequest;
import com.test.pushnotification.response.Response;
import com.test.pushnotification.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/group")
@CrossOrigin("*")
@Tag(name = "Groups Endpoints")
@Slf4j
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping(value = "/create")
    @Operation(summary = "Create new group")
    public ResponseEntity<Response> createNewGroup(@RequestBody GroupRequest groupRequest) {
        return ResponseEntity.ok(groupService.createNewGroup(groupRequest));
    }

    @PostMapping("/message")
    @Operation(summary = "Send a message")
    public ResponseEntity.BodyBuilder sendMessage(@RequestBody GroupMessageRequest request) {
        groupService.sendMessage(request);
        return ResponseEntity.status(HttpStatus.OK);
    }

    @PostMapping(value = "/add/member")
    @Operation(summary = "Add new member to a group")
    public ResponseEntity<Response> addNewMember(@RequestBody GroupMemberRequest groupMemberRequest) {
        return ResponseEntity.ok(groupService.addMember(groupMemberRequest));
    }

    @GetMapping(value = "/get")
    @Operation(summary = "Get group data")
    public ResponseEntity<Response> getGroup(@RequestParam String groupName) {
        return ResponseEntity.ok(groupService.getGroup(groupName));
    }

    @DeleteMapping(value = "/remove/member")
    @Operation(summary = "Remove member to a group")
    public ResponseEntity<Response> removeMember(@RequestBody GroupMemberRequest groupMemberRequest) {
        return ResponseEntity.ok(groupService.removeMember(groupMemberRequest));
    }

    @PostMapping(value = "/leave")
    @Operation(summary = "Leave group")
    public ResponseEntity<Response> leaveGroup(@RequestParam String username, String groupName) {
        return ResponseEntity.ok(groupService.leaveGroup(username, groupName));
    }

    @PutMapping(value = "/assign/role")
    @Operation(summary = "Assign Role To Group Member")
    public ResponseEntity<Response> assignRoleToGroupMember(@RequestBody GroupMemberRequest groupMemberRequest, @RequestParam GroupPermissions permission) {
        return ResponseEntity.ok(groupService.assignRoleToGroupMember(groupMemberRequest, permission));
    }

    @PutMapping(value = "/remove/role")
    @Operation(summary = "Assign Role From Group Member")
    public ResponseEntity<Response> removeRoleFromGroupMember(@RequestBody GroupMemberRequest groupMemberRequest, @RequestParam GroupPermissions permission) {
        return ResponseEntity.ok(groupService.removeRoleFromGroupMember(groupMemberRequest, permission));
    }

    @DeleteMapping(value = "/delete")
    @Operation(summary = "Delete group")
    public ResponseEntity<Response> deleteGroup(@RequestParam String groupName,@RequestParam String admin) {
        return ResponseEntity.ok(groupService.deleteGroup(admin, groupName));
    }
    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe an event")
    public ResponseEntity<Integer> subscribeUser(@RequestParam String groupName,@RequestParam("username") String username, @RequestParam("events") GroupEventType event) {
        groupService.subscribe(groupName,username, event);
        return ResponseEntity.ok(200);
    }

    @PostMapping("/unsubscribe")
    @Operation(summary = "Unsubscribe an event")
    public ResponseEntity<Integer> unsubscribeUser(@RequestParam String groupName,@RequestParam("username") String username, @RequestParam("event") GroupEventType event) {
        groupService.unsubscribe(groupName,username, event);
        return ResponseEntity.ok(200);
    }

    @PostMapping("/unsubscribe/all")
    @Operation(summary = "Unsubscribe all events")
    public ResponseEntity<Integer> unsubscribeUserFromAllEvents(@RequestParam String groupName,@RequestParam("username") String username) {
        groupService.unsubscribeFromAllEvents(groupName,username);
        return ResponseEntity.ok(200);
    }
    @PostMapping("/subscribe/all")
    @Operation(summary = "subscribe all events")
    public ResponseEntity<Integer> subscribeUserFromAllEvents(@RequestParam String groupName,@RequestParam("username") String username) {
        groupService.subscribeFromAllEvents(groupName,username);
        return ResponseEntity.ok(200);
    }
}
