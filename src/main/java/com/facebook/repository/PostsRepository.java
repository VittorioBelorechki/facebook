package com.bacefook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bacefook.entity.Post;

@Repository
public interface PostsRepository extends JpaRepository<Post, Integer> {
	List<Post> findAllByPosterIdOrderByPostingTimeDesc(Integer posterId);

	List<Post> findAllBySharesPostId(Integer sharesPostId);

}
