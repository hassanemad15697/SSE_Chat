package com.test.pushnotification.config;

import com.test.pushnotification.model.Gender;
import com.test.pushnotification.request.GroupMemberRequest;
import com.test.pushnotification.request.GroupRequest;
import com.test.pushnotification.request.UserSignupRequest;
import com.test.pushnotification.service.GroupService;
import com.test.pushnotification.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class DefaultUsers {


    private final UserService userService;
    private final GroupService groupService;

    public DefaultUsers(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

    @Bean
    public void createDefaultUsers() {
        UserSignupRequest hassan = UserSignupRequest.builder()
                .username("hassan")
                .gender(Gender.male)
                .dateOfBirth(Date.from(Instant.now()))
                .email("hassanaskar5@gmail.com")
                .password("password123")
                .profilePicture("")
                .build();
        UserSignupRequest amro = UserSignupRequest.builder()
                .username("amro")
                .gender(Gender.male)
                .dateOfBirth(Date.from(Instant.now()))
                .email("amroaskar5@gmail.com")
                .password("password123")
                .profilePicture("")
                .build();
                UserSignupRequest shams = UserSignupRequest.builder()
                .username("shams")
                .gender(Gender.male)
                .dateOfBirth(Date.from(Instant.now()))
                .email("ahmedshams5@gmail.com")
                .password("password123")
                .profilePicture("")
                .build();
                UserSignupRequest bahaa = UserSignupRequest.builder()
                .username("bahaa")
                .gender(Gender.male)
                .dateOfBirth(Date.from(Instant.now()))
                .email("bahaazenhom5@gmail.com")
                .password("password123")
                .profilePicture("")
                .build();

        userService.addUser(hassan);
        userService.addUser(amro);
        userService.addUser(shams);
        userService.addUser(bahaa);

        GroupRequest group = GroupRequest.builder().groupName("SSE Group").createdBy("hassan").build();
        groupService.createNewGroup(group);

        GroupMemberRequest groupMemberRequest = GroupMemberRequest.builder().groupName("SSE Group").adminName("hassan").memberName("amro").build();
        groupService.addMember(groupMemberRequest);


    }
}
