package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.dto.CommentCreateDTO;
import com.example.servingwebcontent.dto.CommentDTO;
import com.example.servingwebcontent.dto.QuestionDTO;
import com.example.servingwebcontent.enums.CommentTypeEnum;
import com.example.servingwebcontent.service.CommentService;
import com.example.servingwebcontent.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Integer id,
                           Model model){
        QuestionDTO questionDTO = questionService.getById(id);
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);
        List<CommentDTO> commentDTOList = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);
        //累加阅读数
        questionService.incView(id);
        System.out.println(commentDTOList);
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",commentDTOList);
        model.addAttribute("relatedQuestions",relatedQuestions);
        return "question";
    }
}
