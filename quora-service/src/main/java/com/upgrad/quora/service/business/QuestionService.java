package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        return questionDao.createQuestion(questionEntity);
    }

    public List<QuestionEntity> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    public QuestionEntity editQuestion(QuestionEntity questionEntity, String userId) throws InvalidQuestionException, AuthorizationFailedException {
        QuestionEntity question = questionDao.getQuestion(questionEntity.getUuid());
        if (question != null) {
            if (question.getUser().getUuid().equals(userId)) {
                question.setContent(questionEntity.getContent());
                question.setDate(ZonedDateTime.now());
                return questionDao.editQuestion(question);

            } else {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
            }

        } else {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

    }

    public String deleteQuestion(String questionId, UserEntity user) throws InvalidQuestionException, AuthorizationFailedException {
        QuestionEntity question = questionDao.getQuestion(questionId);
        if (question != null) {
            if (question.getUser().getUuid().equals(user.getUuid()) || user.getRole().equals("admin")) {

                return questionDao.deleteQuestion(question.getUuid());

            } else {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
            }

        } else {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

    }

    public List<QuestionEntity> getAllQuestionsByUser(String userId){
       return questionDao.getAllQuestionsByUser(userId);
    }
}
