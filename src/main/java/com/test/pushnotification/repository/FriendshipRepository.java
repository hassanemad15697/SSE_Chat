package com.test.pushnotification.repository;

import com.test.pushnotification.model.new_models.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
}

