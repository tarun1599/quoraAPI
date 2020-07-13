package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UserAuthDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserAuthEntity getUserAuth(String authorizationToken) {
        UserAuthEntity userAuthEntity = null;

        try {
            userAuthEntity = (UserAuthEntity) entityManager.createQuery("select ua from UserAuthEntity ua where ua.accessToken=:accessToken")
                    .setParameter("accessToken", authorizationToken).getSingleResult();
        } catch (Exception e) {
            return null;
        }
        ;
        return userAuthEntity;
    }
}
