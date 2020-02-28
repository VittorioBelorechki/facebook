package com.bacefook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bacefook.entity.Comment;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Integer>{

	 List<Comment> findAllByPostId(Integer posterId);
	
	 List<Comment> findAllByPosterId(Integer userId);
	
	 List<Comment> findAllByCommentedOnIdOrderByPostingTime(Integer commentedOnId);
	
}
