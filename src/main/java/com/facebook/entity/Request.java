package com.bacefook.entity;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class Request {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@NonNull
//	@Column(name = "sender_id")
	@ManyToOne
	private User sender;
	@NonNull
//	@Column(name = "receiver_id")
	@ManyToOne
	private User receiver;
	@NonNull
	private Integer isConfirmed;
}
