package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.dto.QuestionDTO;
import com.example.servingwebcontent.mapper.QuestionMapper;
import com.example.servingwebcontent.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Integer id,
                           Model model){
        QuestionDTO questionDTO = questionService.getById(id);
        //累加阅读数
        questionService.incView(id);
        System.out.println(questionDTO);
        model.addAttribute("question",questionDTO);
        return "question";
    }
}
