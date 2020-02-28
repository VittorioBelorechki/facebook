package com.bacefook.service;

import java.time.LocalDateTime;
import java.util.*;

import com.bacefook.controller.SessionManager;
import com.bacefook.exception.UnauthorizedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bacefook.dao.PostDAO;
import com.bacefook.dto.PostContentDTO;
import com.bacefook.dto.PostDTO;
import com.bacefook.exception.ElementNotFoundException;
import com.bacefook.entity.Post;
import com.bacefook.entity.User;
import com.bacefook.repository.PostsRepository;
import com.bacefook.repository.UsersRepository;

import javax.servlet.http.HttpServletRequest;

@Service
public class PostService {

	@Autowired
	private PostsRepository postsRepo;
	@Autowired
	private PostDAO postDao;
	@Autowired
	private UsersRepository usersRepo;//TODO user service

	@Autowired
	private UserService userService;

	private ModelMapper mapper = new ModelMapper();

	public Integer save(HttpServletRequest request, PostContentDTO contentDto) throws UnauthorizedException, ElementNotFoundException {
		int posterId = SessionManager.getLoggedUser(request);
		String content = contentDto.getContent();
		if (content == null || content.isEmpty()) {
			throw new ElementNotFoundException("Write something before posting!");
		}
		User poster = userService.findById(posterId);
		Post post = new Post(poster, content, LocalDateTime.now());
		return postsRepo.save(post).getId();
	}

	public Integer update(Post post) {
		return postsRepo.save(post).getId();
	}

	public Integer deletePost(Integer id) {
		return postDao.deletePostById(id);
	}

	public Integer saveSharing(Integer sharesPostId, Integer posterId, PostContentDTO postContentDto)
			throws ElementNotFoundException {
	
		if (!existsById(sharesPostId)) {
			throw new ElementNotFoundException("Cannot share a post that does not exist!");
		}
		User poster = userService.findById(posterId);
		Post post = new Post(poster, postContentDto.getContent(), LocalDateTime.now());
		post.setSharesPost(postsRepo.getOne(sharesPostId));
		return postsRepo.save(post).getId();
	}

	private boolean existsById(Integer sharesPostId) {
		return postsRepo.existsById(sharesPostId);
	}

	/**
	 * get all posts by friends ordered
	 **/
	public List<PostDTO> findAllPostsFromFriends(Integer loggerId) {
		List<Integer> postIds = postDao.getAllPostsIdByFriends(loggerId);
		List<PostDTO> posts = new ArrayList<>(postIds.size());
	
		for (Integer id : postIds) {
			Optional<Post> optional = postsRepo.findById(id);
			
			if (optional.isPresent()) {
				Post post = optional.get();
				PostDTO dto = new PostDTO();
				this.mapper.map(post, dto);
				Optional<User> u = usersRepo.findById(post.getPoster().getId());
				
				if (u.isPresent()) {
					User user = u.get();
					dto.setPosterFullName(user.getFullName());
					posts.add(dto);
				}
			}
		}
		return posts;
	}

	public List<PostDTO> findAllByUser(User poster) throws ElementNotFoundException {
		List<Post> posts = postsRepo.findAllByPosterIdOrderByPostingTimeDesc(poster.getId());
		return this.postsConverter(posts, poster.getId());
	}

	public List<PostDTO> findAllByUserId(Integer posterId) throws ElementNotFoundException {
		List<Post> posts = postsRepo.findAllByPosterIdOrderByPostingTimeDesc(posterId);
		return this.postsConverter(posts, posterId);
	}

	private List<PostDTO> postsConverter(List<Post> posts, Integer posterId) throws ElementNotFoundException {
		List<PostDTO> dtos = new LinkedList<>();
		
		for (Post post : posts) {
			PostDTO dto = new PostDTO();
			this.mapper.map(post, dto);
			
			Optional<User> optional = usersRepo.findById(posterId);
			
			if (!optional.isPresent()) {
				throw new ElementNotFoundException("A user with that ID does not exist!");
			}
			
			User user = optional.get();
			dto.setPosterFullName(user.getFullName());
			dtos.add(dto);
		}
		return dtos;
	}

	public boolean isPostedByUserId(Integer posterId, Post post) {
		List<Post> posts = postsRepo.findAllByPosterIdOrderByPostingTimeDesc(posterId);
		return posts.contains(post);
	}

	public Post findById(Integer postId) throws ElementNotFoundException {
			Optional<Post> post = postsRepo.findById(postId);
			if(!post.isPresent()) {
				throw new ElementNotFoundException("No such post!");
			}
			return post.get();
	}

	public List<PostDTO> findAllWhichSharePostId(Integer postId) {
		List<Post> posts = postsRepo.findAllBySharesPostId(postId);
		List<PostDTO> dtos = new LinkedList<>();
	
		for (Post post : posts) {
			PostDTO dto = new PostDTO();
			this.mapper.map(post, dto);
			Optional<User> optional = usersRepo.findById(post.getPoster().getId());
		
			if (optional.isPresent()) {
				String posterFullName = optional.get().getFullName();
				dto.setPosterFullName(posterFullName);
				dtos.add(dto);
			}
		}
		return dtos;
	}

	/**
	 * add row to post_likes table
	 * throws AlreadyContainsException
	 **/
	public void addLikeToPost(int id, HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		//TODO check if already liked
		//get post
		Post post = postsRepo.getOne(id);
		int userId = SessionManager.getLoggedUser(request);
		User user = userService.findById(userId);
		user.getLikedPosts().add(post);
		postsRepo.saveAndFlush(post); //post is read later before commit
		Set<User> likers = post.getLikers();
		likers.add(user);
		usersRepo.save(user);
	}
//	public void likePost(Integer userId, Integer postId) throws AlreadyContainsException {
//		PostLike like = postLikesRepo.findByUserIdAndPostId(userId, postId);
//		if(like!=null) {
//			throw new AlreadyContainsException("You have already liked this post!");
//		}
//		postLikesRepo.register(new PostLike(userId, postId));
//	}
//	public int unlikeAPost(Integer postId, Integer userId) {
//		return postDao.unlikePost(postId, userId);
//	}
//
	/**
	 * get all user who liked post with id, firstName, lastName, friendsCount and
	 * profilePhotoUrl
	 **/
//	public List<UserSummaryDTO> findAllUsersWhoLikedAPost(Integer postId) {
//		List<PostLike> postLikes = postLikesRepo.findAllByPostId(postId);
//		List<UserSummaryDTO> dtos = new ArrayList<>();
//		for (PostLike like : postLikes) {
//			Optional<User> optionalUser = usersRepo.findById(like.getUserId());
//			if (optionalUser.isPresent()) {
//				UserSummaryDTO dto = new UserSummaryDTO();
//				this.mapper.map(optionalUser.get(), dto);
//				dto.setProfilePhotoUrl(profilePhotoDao.findProfilePhotoUrl(optionalUser.get().getId()).get(0));
//				dto.setFriendsCount(userDao.findAllFriendsOf(optionalUser.get().getId()).size());
//				dtos.add(dto);
//			}
//		}
//		return dtos;
//	}

}
