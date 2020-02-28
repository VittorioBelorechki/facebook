//package com.bacefook.entity;
//
//import javax.persistence.*;
//
//import lombok.*;
//
//@Data
//@Entity
//@NoArgsConstructor
//@RequiredArgsConstructor
//@AllArgsConstructor
//@Builder
//@Table(name = "comment_likes")
//public class CommentLike {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Integer id;
//	@NonNull
//	@ManyToOne
//	@JoinColumn(name = "comment_id")
//	private Comment comment;
//	@NonNull
//	@ManyToOne
//	@JoinColumn(name = "user_id")
//	private User user;
//}
