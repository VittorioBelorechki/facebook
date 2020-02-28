package com.bacefook.service;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


import com.bacefook.controller.SessionManager;
import com.bacefook.dto.*;
import com.bacefook.entity.Gender;
import com.bacefook.exception.AlreadyContainsException;
import com.bacefook.exception.UnauthorizedException;
import com.bacefook.utility.UserValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bacefook.dao.UserDAO;
import com.bacefook.exception.ElementNotFoundException;
import com.bacefook.exception.InvalidUserCredentialsException;
import com.bacefook.entity.User;
import com.bacefook.entity.UserInfo;
import com.bacefook.repository.GenderRepository;
import com.bacefook.repository.UsersInfoRepository;
import com.bacefook.repository.UsersRepository;
import com.bacefook.security.Cryptography;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserService {

    private static final int MIN_PHONE_LENGTH = 5;
    @Autowired
    private UsersRepository usersRepo;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private GenderRepository genderService;
    @Autowired
    private UsersInfoRepository usersInfoRepo;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private RelationService relationService;
    private ModelMapper mapper = new ModelMapper();


    public String findProfilePhotoUrl(Integer userId) throws ElementNotFoundException {
        User user = this.findById(userId);

        String url = user.getUserInfo().getProfilePhotoId();
        if (url.isEmpty()) {
            throw new ElementNotFoundException("No profile picture for this user!");
        }
        return url;
    }


    private boolean emailIsTaken(String email) {
        return usersRepo.findByEmail(email) != null;
    }

    public User findById(Integer id) throws ElementNotFoundException {
        Optional<User> user = usersRepo.findById(id);
        if (!user.isPresent()) {
            throw new ElementNotFoundException("A user with that ID does not exist!");
        }
        return user.get();
    }


    /**
     * find search matches show them by Profile picture, Full name and Friends count
     **/
    public List<UserSummaryDTO> searchByNameOrderedAndLimited(String search, HttpServletRequest request) throws UnauthorizedException {
        Integer userId = SessionManager.getLoggedUser(request);
        List<Integer> ids = userDAO.getAllSearchingMatchesOrderedByIfFriend(userId, search);
        List<UserSummaryDTO> usersDTO = new LinkedList<>();
        for (Integer integer : ids) {
            Optional<User> user = usersRepo.findById(integer);
            if (user.isPresent()) {
                UserSummaryDTO dto = new UserSummaryDTO();
                this.mapper.map(user.get(), dto);
                dto.setFriendsCount(relationService.getFriendsCountOF(user.get().getId()));
                // TODO maybe set photo URL
                usersDTO.add(dto);
            }
        }
        return usersDTO;
    }

    public String changePassword(ChangePasswordDTO passDto, HttpServletRequest request)
            throws ElementNotFoundException, NoSuchAlgorithmException, InvalidUserCredentialsException, UnauthorizedException {
        UserValidator.validate(passDto);
        int userId = SessionManager.getLoggedUser(request);
        User user = findById(userId);
        String oldPass = Cryptography.cryptSHA256(passDto.getOldPassword());
        // TODO implement safer equals
        if (user.getPassword().equals(oldPass)) {
            user.setPassword(Cryptography.cryptSHA256(passDto.getNewPassword()));
            usersRepo.save(user);
            return "Password successfylly changed!";
        } else {
            throw new InvalidUserCredentialsException("Incorrect password!");
        }
    }

    public Integer login(LoginDTO login, HttpServletRequest request) throws InvalidUserCredentialsException, ElementNotFoundException, NoSuchAlgorithmException, UnauthorizedException {
        if (!SessionManager.isLogged(request)) {
            UserValidator.validate(login);
            String email = login.getEmail();
            User user = usersRepo.findByEmail(email);
            if (user == null) {
                throw new ElementNotFoundException("A user with that email does not exist!");
            }
            if (user.getPassword().equals(Cryptography.cryptSHA256(login.getPassword()))) {
                SessionManager.signInUser(request, user);
                return user.getId();
            } else {
                throw new InvalidUserCredentialsException("Wrong login credentials!");
            }
        } else {
            throw new UnauthorizedException("Log out first before you log in!");
        }
    }

    public User register(SignUpDTO signUp, HttpServletRequest request) throws NoSuchAlgorithmException, InvalidUserCredentialsException, UnauthorizedException {
        UserValidator.validate(signUp);
        if (SessionManager.isLogged(request)) {
            throw new UnauthorizedException("Please log out before you can register!");
        }
        if (this.emailIsTaken(signUp.getEmail())) {
            throw new InvalidUserCredentialsException("That email is already taken!");
        }
        User user = new User();
        this.mapper.map(signUp, user);
        user.setPassword(Cryptography.cryptSHA256(signUp.getPassword()));
        Gender gender = genderService.findByGenderName(signUp.getGender());
        user.setGender(gender);
        usersRepo.save(user);
        SessionManager.signInUser(request, user);
        return user;
    }

    public String logout(HttpServletRequest request) throws UnauthorizedException {
        if (SessionManager.isLogged(request)) {
            return SessionManager.logOutUser(request);
        } else {
            throw new UnauthorizedException("You are not logged in!");
        }
    }

    //TODO saveInfo
    private UserInfo register(UserInfo info) throws ElementNotFoundException {
        Optional<User> user = usersRepo.findById(info.getId());
        if (!user.isPresent()) {
            throw new ElementNotFoundException("No such user! Register before you can setup your profile.");
        }
        if (info.getProfilePhotoId() != null
                && !photoService.getIfUserHasPhotoById(info.getId(), info.getProfilePhotoId())) {
            throw new ElementNotFoundException("You are not the owner of this photo!");
        }
        if (info.getCoverPhotoId() != null
                && !photoService.getIfUserHasPhotoById(info.getId(), info.getCoverPhotoId())) {
            throw new ElementNotFoundException("You are not the owner of this photo!");
        }
        return usersInfoRepo.save(info);
    }

    public UserInfo setUpProfile(UserInfoDTO userInfoDto, HttpServletRequest request) throws UnauthorizedException, AlreadyContainsException, ElementNotFoundException, InvalidUserCredentialsException {
        int userId = SessionManager.getLoggedUser(request);
        UserInfo userInfo = getInfoByPhone(userInfoDto.getPhone());
        if (userInfo != null) {
            throw new AlreadyContainsException("A user with that phone already exists");
        }
        UserInfo info = new UserInfo();
        this.mapper.map(userInfoDto, info);
        info.setId(userId);
        this.register(info);
        return info;
    }


    public UserInfoDTO getInfoByUserId(Integer userId) throws ElementNotFoundException {
        if (userId == null) {
            throw new ElementNotFoundException("User id must not be null!");
        }
        Optional<UserInfo> info = usersInfoRepo.findById(userId);
        if (!info.isPresent()) {
            throw new ElementNotFoundException("No additional info for this user!");
        }
        UserInfo userInfo = info.get();
        UserInfoDTO dto = new UserInfoDTO();
        this.mapper.map(userInfo, dto);
        return dto;
    }

    UserInfo findUserInfo(Integer userId) throws ElementNotFoundException {
        if (userId == null) {
            throw new ElementNotFoundException("User id must not be null!");
        }
        Optional<UserInfo> info = usersInfoRepo.findById(userId);
        if (!info.isPresent()) {
            throw new ElementNotFoundException("No additional info for this user!");
        }
        return info.get();

    }

    private UserInfo getInfoByPhone(String phone) throws InvalidUserCredentialsException {
        if (phone == null || phone.trim().length() < MIN_PHONE_LENGTH) {
            throw new InvalidUserCredentialsException("Invalid phone number");
        }
        return usersInfoRepo.findByPhone(phone);
    }


}
