package com.bacefook.entity;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "photos")
public class Photo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@NonNull
	@OneToOne
	@MapsId
	private Post post;
	@NonNull
	private String url;

	@OneToOne(mappedBy = "profilePhoto")
	private UserInfo profilePhotoOF;
	@OneToOne(mappedBy = "coverPhoto")
	private UserInfo coverPhotoOF;
}
