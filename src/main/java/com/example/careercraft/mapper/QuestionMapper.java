//package com.example.careercraft.mapper;
//
//import com.example.careercraft.entity.Question;
//import com.example.careercraft.response.AnswerResponse;
//import com.example.careercraft.response.QuestionResponse;
//import org.springframework.stereotype.Component;
//
//import java.util.stream.Collectors;
//
//@Component
//public class QuestionMapper {
//
//    public   QuestionResponse toQuestionResponse(Question question) {
//        QuestionResponse response = new QuestionResponse();
//        response.setId(question.getId());
//        response.setText(question.getText());
//        response.setJobId(question.getJob().getId());
//        response.setAnswers(question.getAnswers().stream()
//                .map(answer -> {
//                    AnswerResponse answerResponse = new AnswerResponse();
//                    answerResponse.setId(answer.getId());
//                    answerResponse.setText(answer.getText());
//                    answerResponse.setScore(answer.getScore());
//                    answerResponse.setPriority(answer.getPriority());
//                    answerResponse.setOrderValue(answer.getOrderValue());
//                    return answerResponse;
//                })
//                .collect(Collectors.toList()));
//        return response;
//    }
//}