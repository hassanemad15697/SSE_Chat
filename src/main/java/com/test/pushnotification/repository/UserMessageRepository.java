package com.test.pushnotification.repository;

import com.test.pushnotification.model.new_models.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMessageRepository extends JpaRepository<UserMessage, Integer> {
}

