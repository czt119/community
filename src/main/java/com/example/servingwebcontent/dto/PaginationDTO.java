package com.example.servingwebcontent.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer page;
    private Integer totalPage;
    private List<Integer> pages = new ArrayList<>();

    //page为当前页面 size为每个页面多少条记录
    public void setPagination(Integer totalCount, Integer page, Integer size) {
        // 总页数
        totalPage = 0;
        if (totalCount % size == 0){
            totalPage = totalCount / size;
        } else {
           totalPage = totalCount / size + 1;
        }
        if(page < 1)page = 1;
        if(page > totalPage) page = totalPage;
            this.page = page;
        for (int i = -3; i <= 3; i++) {
            if(page + i >= 1 &&  page + i <= totalPage){
                pages.add(page+i);
            }
        }
        // 是否展示上一页
        if(page == 1){
            showPrevious = false;
        }else{
            showPrevious = true;
        }

        // 是否展示下一页
        if(page == totalPage){
            showNext = false;
        }else{
            showNext = true;
        }

        // 是否展示第一页
        if(pages.contains(1)){
            showFirstPage = false;
        } else {
            showFirstPage = true;
        }

        //是否展示最后一页
        if(pages.contains(totalPage)){
            showEndPage = false;
        } else {
            showEndPage = true;
        }
    }
}
