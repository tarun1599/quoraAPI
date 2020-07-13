package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.UserAuthService;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class QuestionController {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private UserService userService;

    @Autowired
    private com.upgrad.quora.service.business.QuestionService questionService;

    @RequestMapping(value = "/question", method = RequestMethod.POST)
    public String question() {
        return "question";
    }


    @RequestMapping(method = RequestMethod.POST, value = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestBody final QuestionRequest questionRequest,
                                                           @RequestHeader final String authToken) {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setDate(ZonedDateTime.now());
        //questionEntity.setUser();

        QuestionEntity questionEntityRes = questionService.createQuestion(questionEntity);

        QuestionResponse questionResponse = new QuestionResponse().id(questionEntityRes.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);

    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionEntity>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        String[] bearerToken = authorization.split("Bearer ");
        userAuthService.getUserByAuth(bearerToken[1]);

        List<QuestionEntity> listOfQuestions = questionService.getAllQuestions();

        return new ResponseEntity<List<QuestionEntity>>(listOfQuestions, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, @PathVariable String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        String[] bearerToken = authorization.split("Bearer ");
        UserAuthEntity userAuthEntity = userAuthService.getUserByAuth(bearerToken[1]);

        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionEditRequest.getContent());
        questionEntity.setUuid(questionId);

        QuestionEntity questionResponse = questionService.editQuestion(questionEntity, userAuthEntity.getUser_id().getUuid());

        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(questionResponse.getUuid()).status("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        String[] bearerToken = authorization.split("Bearer ");
        UserAuthEntity userAuthEntity = userAuthService.getUserByAuth(bearerToken[1]);

        String deleteResponse = questionService.deleteQuestion(questionId, userAuthEntity.getUser_id());

        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(deleteResponse).status("QUESTION DELETED");

        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionEntity>> getAllQuestionsByUser (@PathVariable String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException, UserNotFoundException {
        userService.getUser(userId);

        String[] bearerToken = authorization.split("Bearer ");
        UserAuthEntity userAuthEntity = userAuthService.getUserByAuth(bearerToken[1]);

        List<QuestionEntity> quesList= questionService.getAllQuestionsByUser(userId);

        return new ResponseEntity<List<QuestionEntity>>(quesList, HttpStatus.OK);
    }

}
