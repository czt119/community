package com.example.servingwebcontent.advice;

import com.example.servingwebcontent.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class CustomizeExceptionHandler {
    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable e, Model model) {
        if( e instanceof CustomizeException){
            model.addAttribute("message","你的问题出错了");
        } else {
            model.addAttribute("message","服务器冒烟了,请稍后再试");
        }
        return new ModelAndView("error");
    }
}
