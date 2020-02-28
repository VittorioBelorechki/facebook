package com.bacefook.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bacefook.dto.PhotoDTO;
import com.bacefook.exception.ElementNotFoundException;
import com.bacefook.exception.UnauthorizedException;
import com.bacefook.exception.UnprocessableFileException;
import com.bacefook.service.PhotoService;

//@CrossOrigin(origins = "http://bacefook.herokuapp.com")
@RestController
public class PhotosController {

    @Autowired
    private PhotoService photoService;

    @PostMapping("photos/uploadphoto")//DONE
    public PhotoDTO uploadPhoto(@RequestParam MultipartFile input, HttpServletRequest request,
                                HttpServletResponse response)
            throws UnprocessableFileException, UnauthorizedException, ElementNotFoundException {
        return photoService.save(input, request);
    }

    @PutMapping("/profilephotos/{photoId}")//DONE
    public String updateProfilePhoto(@PathVariable Integer photoId, HttpServletRequest request)
            throws UnauthorizedException, ElementNotFoundException {
        photoService.updateProfilePhoto(photoId, request);
        return "You changed your profile photo successfully!";
    }

    @PutMapping("/coverphotos/{photoId}")//DONE
    public void updateCoverPhoto(@PathVariable Integer photoId, HttpServletRequest request)
            throws UnauthorizedException, ElementNotFoundException {
        photoService.updateCoverPhoto(photoId, request);
    }

    @GetMapping("/users/{userId}/photos")//DONE
    public List<PhotoDTO> getAllPhotosOfUser(@PathVariable Integer userId) {
        return photoService.getAllPhotosOfUser(userId);
    }
}
