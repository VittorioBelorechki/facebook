package com.bacefook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bacefook.entity.Request;

@Repository
public interface RelationsRepository extends JpaRepository<Request, Integer> {
	
	Request findBySenderIdAndReceiverId(Integer senderId, Integer receiverId);
	
}
