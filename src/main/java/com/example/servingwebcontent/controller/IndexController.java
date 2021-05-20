package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.dto.PaginationDTO;
import com.example.servingwebcontent.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name="page",defaultValue = "1") Integer page,
                        @RequestParam(name="size",defaultValue = "5") Integer size){

        PaginationDTO pagination = questionService.list(page,size);
        System.out.println(pagination);
        model.addAttribute("pagination",pagination);
        return "index";
    }
}
