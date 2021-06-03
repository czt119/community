package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.dto.NotificationDTO;
import com.example.servingwebcontent.dto.PaginationDTO;
import com.example.servingwebcontent.enums.NotificationTypeEnum;
import com.example.servingwebcontent.model.Notification;
import com.example.servingwebcontent.model.User;
import com.example.servingwebcontent.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "id")Integer id){

        User user = (User)request.getSession().getAttribute("user");
        if(user == null){
            return "redirect:/";
        }
        NotificationDTO notificationDTO = notificationService.read(id,user);

        System.out.println("id:"+id);
        System.out.println("user:"+user);
        if(NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()
            || NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType())
        return "redirect:/question/" + notificationDTO.getOuterId();
        return "redirect:/";
    }
}
