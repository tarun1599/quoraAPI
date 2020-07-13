package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createQuery("select q from QuestionEntity q").getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity getQuestion(String questionId) {
        try {
            return entityManager.createQuery("select q from QuestionEntity q where q.uuid=:questionId", QuestionEntity.class).setParameter("questionId", questionId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity editQuestion(QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
        return questionEntity;
    }

    public String deleteQuestion(String questionid) {
        entityManager.createQuery("delete from QuestionEntity q where q.uuid=:questionid").setParameter("questionid", questionid);
        return questionid;
    }

    public List<QuestionEntity> getAllQuestionsByUser(String userId) {
        try {
            return entityManager.createQuery("select q from QuestionEntity q where q.user.uuid=:userId", QuestionEntity.class).setParameter("userId", userId).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
