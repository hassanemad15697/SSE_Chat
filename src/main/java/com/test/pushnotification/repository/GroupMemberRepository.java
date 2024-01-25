package com.test.pushnotification.repository;

import com.test.pushnotification.model.new_models.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {
}
