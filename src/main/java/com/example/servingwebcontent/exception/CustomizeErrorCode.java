package com.example.servingwebcontent.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{

    QUESTION_NOT_FOUND(2001,"你找的问题不在了"),
    TARGET_PARAM_NOT_FOUND(2002,"未选中问题或评论进行回复"),
    NO_LOGIN(2003,"当前操作需要登录,请登录后重试"),
    SYS_ERROR(2004,"服务器冒烟了请稍后再试"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006,"评论不存在"),
    CONTENT_IS_EMPTY(2007,"输入内容不能为空"),
    READ_NOTIFICATION_FAIL(2008,"兄弟你这是读别人的信息"),
    NOTIFICATION_NOT_FOUND(2009,"你的消息不存在了");


    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    private String message;
    private Integer code = 200;

    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }
}
