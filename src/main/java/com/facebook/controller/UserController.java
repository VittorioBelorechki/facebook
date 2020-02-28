package com.bacefook.controller;

import java.io.IOException;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bacefook.dto.ChangePasswordDTO;
import com.bacefook.dto.LoginDTO;
import com.bacefook.dto.SignUpDTO;
import com.bacefook.dto.UserInfoDTO;
import com.bacefook.dto.UserSummaryDTO;
import com.bacefook.exception.AlreadyContainsException;
import com.bacefook.exception.ElementNotFoundException;
import com.bacefook.exception.InvalidUserCredentialsException;
import com.bacefook.exception.UnauthorizedException;
import com.bacefook.service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public void startingPage(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://documenter.getpostman.com/view/6800789/S11RKak4");
    }

    @GetMapping("/users/search")//DONE
    public List<UserSummaryDTO> getAllUsersBySearch(@RequestParam String input, HttpServletRequest request) throws UnauthorizedException {
        return userService.searchByNameOrderedAndLimited(input, request);
    }

    @PostMapping("/users/changepassword")//DONE
    public String changeUserPassword(@RequestBody ChangePasswordDTO passDto, HttpServletRequest request)
            throws InvalidUserCredentialsException, NoSuchAlgorithmException, UnauthorizedException,
            ElementNotFoundException {
        return userService.changePassword(passDto, request);//TODO response entity
    }

    @PostMapping("/users/signup") //DONE
    public Integer signUp(@RequestBody SignUpDTO signUp, HttpServletRequest request, HttpServletResponse response)
            throws InvalidUserCredentialsException, NoSuchAlgorithmException,
            UnauthorizedException {
        return userService.register(signUp, request).getId();
        //TODO response entity
    }

    @PostMapping("/users/login") //DONE
    public Integer login(@RequestBody LoginDTO login, HttpServletRequest request)
            throws InvalidUserCredentialsException, NoSuchAlgorithmException, ElementNotFoundException,
            UnauthorizedException {
        return userService.login(login, request);//TODO response entity
    }

    @PostMapping("/users/logout")//DONE
    public String logout(HttpServletRequest request) throws UnauthorizedException {
        return userService.logout(request);
    }

    @PostMapping("/users/setup")
    public Integer setUpProfile(@RequestBody UserInfoDTO infoDto, HttpServletRequest request) throws ElementNotFoundException, AlreadyContainsException, InvalidUserCredentialsException, UnauthorizedException {
        return userService.setUpProfile(infoDto, request).getId();
    }
}
