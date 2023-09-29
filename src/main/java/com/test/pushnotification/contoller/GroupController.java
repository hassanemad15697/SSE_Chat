package com.test.pushnotification.contoller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.pushnotification.model.Group;
import com.test.pushnotification.request.GroupRequest;
import com.test.pushnotification.request.NewMemberRequest;
import com.test.pushnotification.response.GroupMemberResponse;
import com.test.pushnotification.response.GroupResponse;
import com.test.pushnotification.response.Response;
import com.test.pushnotification.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    @PostMapping(value = "/add/member")
    @Operation(summary = "Add new member to a group")
    public ResponseEntity<Response> addNewMember(@RequestBody NewMemberRequest newMemberRequest) {
        return ResponseEntity.ok( groupService.addMember(newMemberRequest));
    }


}
