package com.bacefook.service;

import java.time.LocalDateTime;
import java.util.*;

import com.bacefook.controller.SessionManager;
import com.bacefook.entity.Post;
import com.bacefook.entity.User;
import com.bacefook.exception.AlreadyContainsException;
import com.bacefook.exception.UnauthorizedException;
import com.bacefook.repository.PostsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bacefook.dto.CommentContentDTO;
import com.bacefook.dto.CommentDTO;
import com.bacefook.exception.ElementNotFoundException;
import com.bacefook.entity.Comment;
import com.bacefook.repository.CommentsRepository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Service
public class CommentService {

    @Autowired
    private CommentsRepository commentsRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private PostsRepository postsRepository;
    private ModelMapper mapper = new ModelMapper();

    public Integer save(HttpServletRequest request, @RequestParam("postId") Integer postId, @RequestBody CommentContentDTO commentContentDto) throws UnauthorizedException, ElementNotFoundException {
        int posterId = SessionManager.getLoggedUser(request);
        Post post = postService.findById(postId);
        if (commentContentDto.getContent().isEmpty()) {
            throw new ElementNotFoundException("Cannot add comment with empty content!");
        }
        Comment comment = new Comment(posterId, postService.findById(postId), commentContentDto.getContent(), LocalDateTime.now());
        post.addComment(comment);
        postsRepository.save(post);//TODO check and replace postsRepo with service
        return 0;//TODO return
    }

    public Integer update(HttpServletRequest request, Integer commentId, @RequestBody CommentContentDTO content) throws ElementNotFoundException, UnauthorizedException {
        if (SessionManager.isLogged(request)) {
            if (content.getContent().isEmpty()) {
                throw new ElementNotFoundException("Cannot update comment with empty content!");
            }
            Comment comment = this.findById(commentId);
            comment.setContent(content.getContent());
            return commentsRepo.save(comment).getId();
        } else {
            throw new UnauthorizedException("You are not logged in! Please log in before trying to update your posts.");
        }
    }

    public void deleteComment(Integer id, HttpServletRequest request) throws ElementNotFoundException, UnauthorizedException {
        int userId = SessionManager.getLoggedUser(request);
        List<Comment> comments = this.findAllByUserId(userId);
        if (!comments.contains(this.findById(id))) {
            throw new ElementNotFoundException("User have no rights for this comment!");
        }
        commentsRepo.deleteById(id);
        //TODO response entity
    }

    public List<CommentDTO> findAllByPostId(Integer postId) throws ElementNotFoundException {
        /**
         *  checks if post exists
         **/
        postService.findById(postId);
        List<Comment> comments = commentsRepo.findAllByPostId(postId);
        List<CommentDTO> commentsOnPost = new ArrayList<>();

        for (Comment comment : comments) {
            String posterFullName = userService.findById(comment.getPosterId()).getFullName();
            CommentDTO commentDto = new CommentDTO();
            this.mapper.map(comment, commentDto);
            commentDto.setPosterFullName(posterFullName);
            commentDto.setComentedOnId(comment.getCommentedOn().getId());
            commentsOnPost.add(commentDto);
        }
        return commentsOnPost;
    }

    private List<Comment> findAllByUserId(Integer userId) {
        return commentsRepo.findAllByPosterId(userId);
    }

    public List<CommentDTO> findAllRepliesTo(Integer commentId) throws ElementNotFoundException {
        /**
         * checks if comment exists
         */
        this.findById(commentId);
        List<Comment> commentReplies = commentsRepo.findAllByCommentedOnIdOrderByPostingTime(commentId);
        List<CommentDTO> replies = new ArrayList<>(commentReplies.size());
        for (Comment comment : commentReplies) {
            String posterFullName = userService.findById(comment.getPosterId()).getFullName();
            CommentDTO dto = new CommentDTO();
            this.mapper.map(comment, dto);
            dto.setPosterFullName(posterFullName);
            dto.setComentedOnId(comment.getCommentedOn().getId());
            replies.add(dto);
        }
        return replies;
    }

    public void replyTo(HttpServletRequest request, Integer commentId, CommentContentDTO commentContentDto)
            throws ElementNotFoundException, UnauthorizedException {
        Integer userId = SessionManager.getLoggedUser(request);
        Comment reply = new Comment(userId, findById(commentId).getPost(),
                commentContentDto.getContent(), LocalDateTime.now());
        reply.setCommentedOn(commentsRepo.getOne(commentId));
        commentsRepo.save(reply);
    }

    public void addLikeToComment(int commentId, HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException, AlreadyContainsException {
        int userId = SessionManager.getLoggedUser(request);
        User user = userService.findById(userId);
        Comment comment = this.findById(commentId);
        if (this.checkIfCommentIsLiked(comment, user)) {
            throw new AlreadyContainsException("You have already liked this comment");
        }
        user.getLikedComments().add(comment);
        Set<User> likers = comment.getUsers();
        likers.add(user);
        commentsRepo.save(comment);
    }

    private Comment findById(Integer commentId) throws ElementNotFoundException {
        Comment comment = commentsRepo.getOne(commentId);
        if (comment == null) {
            throw new ElementNotFoundException("No such comment!");
        }
        return comment;
    }

    public int unlikeAComment(Integer commentId, Integer userId) throws ElementNotFoundException {
        User user = userService.findById(userId);
        Comment comment = this.findById(commentId);
        if (!this.checkIfCommentIsLiked(comment, user)) {
            throw new ElementNotFoundException("You can't unlike comment, which you haven't liked!");
        }
        user.getLikedComments().remove(comment);
        Set<User> likers = comment.getUsers();
        likers.remove(user);
        commentsRepo.save(comment);

        return 0;//TODO return if deleted
    }

    private boolean checkIfCommentIsLiked(Comment comment, User user) {
        Set<Comment> likedComments = user.getLikedComments();
        return likedComments.contains(comment);
    }



}
