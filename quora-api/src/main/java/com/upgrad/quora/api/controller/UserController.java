package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;


@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
   private UserBusinessService userBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

        final UserEntity userEntity = new UserEntity();

        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        // userEntity.setSalt("xyz123abc");  // No need as its encrypted and stored now
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAmoutme(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        userEntity.setContactnumber(signupUserRequest.getContactNumber());

       final UserEntity createdUserEntity = userBusinessService.signup(userEntity);
       SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("REGISTERED");
       return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException, SignOutRestrictedException {

        final UserAuthEntity userAuth = userBusinessService.signout(authorization.split("Bearer ")[1]);

        SignoutResponse userResponse = new SignoutResponse().id(userAuth.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(userResponse, HttpStatus.CREATED);
    }


        @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);

        String[] decodedArray = decodedText.split(":");

        UserAuthEntity userAuth =userBusinessService.signin(decodedArray[0],decodedArray[1]);

        UserEntity user = userAuth.getUser_id();

        SigninResponse authorizedUserResponse =  new SigninResponse().id(user.getUuid())
                .message("SIGNED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuth.getAccessToken());
        return new ResponseEntity<SigninResponse>(authorizedUserResponse,headers, HttpStatus.OK);

    }

}
