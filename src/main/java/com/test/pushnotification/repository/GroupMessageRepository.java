package com.test.pushnotification.repository;

import com.test.pushnotification.model.new_models.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Integer> {
}
