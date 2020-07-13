package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserQuestionDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserQuestionDao userQuestionDao;

    public UserEntity getUser(String userId) throws UserNotFoundException {
        UserEntity user= userQuestionDao.getUser(userId);
        if(user!=null){
            return user;
        }else {
            throw new UserNotFoundException("USR-001","User with entered uuid whose question details are to be seen does not exist");
        }
    }


}
