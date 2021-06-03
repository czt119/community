package com.example.servingwebcontent.dto;

import lombok.Data;

@Data
public class QuestionQueryDTO {
    private String search;
    private Integer size;
    private Integer page;
}
