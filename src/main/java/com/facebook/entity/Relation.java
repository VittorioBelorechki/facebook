package com.bacefook.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "relations")
public class Relation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	@NonNull
	@Column(name = "sender_id")
	private Integer senderId;
	@NonNull
	@Column(name = "receiver_id")
	private Integer receiverId;
	@NonNull
	@Column(name="is_confirmed")
	private Integer isConfirmed;
}
