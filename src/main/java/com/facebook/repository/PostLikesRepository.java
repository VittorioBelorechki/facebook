package com.bacefook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bacefook.entity.PostLike;

@Repository
public interface PostLikesRepository extends JpaRepository<PostLike, Integer> {
	
	List<PostLike> findAllByPostId(Integer postId);
	
	PostLike findByUserIdAndPostId(Integer userId,Integer postId);
	
}
