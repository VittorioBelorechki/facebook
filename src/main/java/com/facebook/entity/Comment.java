package com.bacefook.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@NonNull
	@Column(nullable = false)
	private Integer posterId;
	@NonNull
	@Column(nullable = false)
	private Integer postId;
	@Column
	private Integer commentedOnId;
	@NonNull
	@Column(nullable = false)
	private String content;
	@NonNull
	@Column(nullable = false)
	private LocalDateTime postingTime;

	@ManyToMany(mappedBy = "users")
	private Set<User> users = new HashSet<>();

//	//Bidirectional mapping
//	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//	private Set<CommentLike> commentsLikes;

}
