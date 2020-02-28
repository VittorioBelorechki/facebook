
package com.bacefook.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@NonNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gender_id")
	private Gender gender;
	@NonNull
	@Column(unique=true, nullable = false)
	private String email;
	@NonNull
	private String firstName;
	@NonNull
	private String lastName;
	@NonNull
	private String password;
	@NonNull
	private LocalDate birthday;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private UserInfo userInfo;

	@OneToMany(
			mappedBy = "poster",
			cascade = CascadeType.ALL,
			orphanRemoval = true
	)
	@JsonBackReference
	private Set<Post> posts = new HashSet<>();


	@ManyToMany
	@JoinTable(
			name = "comments_like",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "comment_id"))
	@JsonBackReference
	private Set<Comment> likedComments = new HashSet<>();//TODO add

	@ManyToMany
	@JoinTable(
			name = "post_likes",
			joinColumns =  @JoinColumn(name="user_id"),
			inverseJoinColumns = @JoinColumn(name = "post_id")
	)
	@JsonBackReference
	private Set<Post> likedPosts = new HashSet<>();

	@OneToMany
//			(
//			mappedBy = "post",
//			cascade = CascadeType.ALL,
//			orphanRemoval = true
//	)
	@JsonBackReference
	Set<User> usersFromRequests = new TreeSet<>();
	
	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof User)) return false;
		User user = (User) o;
		return Objects.equals(id, user.id) &&
				Objects.equals(gender, user.gender) &&
				Objects.equals(email, user.email) &&
				Objects.equals(firstName, user.firstName) &&
				Objects.equals(lastName, user.lastName) &&
				Objects.equals(password, user.password) &&
				Objects.equals(birthday, user.birthday);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, gender, email, firstName, lastName, password, birthday);
	}


}
