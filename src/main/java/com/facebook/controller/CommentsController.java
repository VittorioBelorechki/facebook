package com.bacefook.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bacefook.dto.CommentContentDTO;
import com.bacefook.dto.CommentDTO;
import com.bacefook.exception.AlreadyContainsException;
import com.bacefook.exception.ElementNotFoundException;
import com.bacefook.exception.UnauthorizedException;
import com.bacefook.entity.Comment;
import com.bacefook.service.CommentService;
import com.bacefook.service.PostService;
import com.bacefook.service.UserService;

@RestController
public class CommentsController {
    @Autowired
    private CommentService commentsService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    @PostMapping("/commentlikes")//DONE
    public String addLikeToComment(@RequestParam("commentId") Integer commentId, HttpServletRequest request)
            throws UnauthorizedException, ElementNotFoundException, AlreadyContainsException {
        commentsService.addLikeToComment(commentId, request);
        return "Comment " + commentId + " was liked";
    }

    @DeleteMapping("/commentlikes/unlike")
    public String unlikeAComment(@RequestParam("commentId") Integer commentId, HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
        int userId = SessionManager.getLoggedUser(request);
        int rows = commentsService.unlikeAComment(commentId, userId);
//		if(rows>0) {
//			return "Comment was unliked!";
//		}else {
//			return "Could not unlike comment.";
//		}
        return "Comment was unliked!";
    }

    @PostMapping("/commentreply")//DONE
    public void addReplyToComment(@RequestParam("commentId") Integer commentId,
                                  @RequestBody CommentContentDTO commentContentDto, HttpServletRequest request)
            throws UnauthorizedException, ElementNotFoundException {
        commentsService.replyTo(request, commentId, commentContentDto);
    }

    @PostMapping("/comments")//DONE
    public Integer addCommentToPost(@RequestParam("postId") Integer postId,
                                    @RequestBody CommentContentDTO commentContentDto, HttpServletRequest request)
            throws UnauthorizedException, ElementNotFoundException {
        return commentsService.save(request, postId, commentContentDto);
    }

    @PutMapping("/comments")//DONE
    public void updateComment(@RequestParam("commentId") Integer commentId, @RequestBody CommentContentDTO content,
                              HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
        commentsService.update(request, commentId, content);
    }

    @DeleteMapping("/comments/delete")//DONE
    public String deleteComment(@RequestParam("commentId") Integer id, HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
        commentsService.deleteComment(id, request);
        return "Comment was deleted";
    }

    @GetMapping("/comments")//DONE
    public List<CommentDTO> getAllCommentsByPost(@RequestParam("postId") Integer postId)
            throws ElementNotFoundException {
        return commentsService.findAllByPostId(postId);
    }

    @GetMapping("/commentreplies")//DONE
    public List<CommentDTO> getAllCommentReplies(@RequestParam("commentId") Integer commentId)
            throws ElementNotFoundException {
        return commentsService.findAllRepliesTo(commentId);
    }

}
