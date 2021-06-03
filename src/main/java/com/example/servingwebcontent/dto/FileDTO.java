package com.example.servingwebcontent.dto;

import lombok.Data;

@Data
public class FileDTO {
    private int success;
    private String message;
    private String url;
}
