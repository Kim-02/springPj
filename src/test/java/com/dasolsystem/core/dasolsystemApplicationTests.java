package com.dasolsystem.core;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class dasolsystemApplicationTests {
    @Autowired
    private QuestionRepository questionRepository;

    @Test
    void testJpa(){
        Question q1 = new Question();
        q1.setSubject("dasolsystem");
        q1.setContent("dasolsystem is connecting");
        q1.setCreateDate(LocalDateTime.now());
        questionRepository.save(q1);

        Question q2 = new Question();
        q2.setSubject("dasolsystem2");
        q2.setContent("dasolsystem is connecting to database");
        q2.setCreateDate(LocalDateTime.now());
        questionRepository.save(q2);

    }

    @Test
    void contextLoads() {
        List<Question> questions = questionRepository.findAll();
        assertEquals(2, questions.size());

        Question q = questions.get(0);
        assertEquals("dasolsystem", q.getSubject());
    }
}