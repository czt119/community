package com.example.servingwebcontent.service;

import com.example.servingwebcontent.dto.PaginationDTO;
import com.example.servingwebcontent.dto.QuestionDTO;
import com.example.servingwebcontent.dto.QuestionQueryDTO;
import com.example.servingwebcontent.exception.CustomizeErrorCode;
import com.example.servingwebcontent.exception.CustomizeException;
import com.example.servingwebcontent.mapper.QuestionExtMapper;
import com.example.servingwebcontent.mapper.QuestionMapper;
import com.example.servingwebcontent.mapper.UserMapper;
import com.example.servingwebcontent.model.Question;
import com.example.servingwebcontent.model.QuestionExample;
import com.example.servingwebcontent.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;


    public PaginationDTO list(String search,Integer page, Integer size) {
            Integer offset = size * (page-1);
            QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
            questionQueryDTO.setSearch(search);
            //问题的个数
            Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);
            List<QuestionDTO> questionDTOList = new ArrayList<>();
            List<Question> questions;
        if(search.equals("")){
            questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(),new RowBounds(offset,size));
        }else{
            questionQueryDTO.setPage(offset);
            questionQueryDTO.setSize(size);
            questions = questionExtMapper.selectByQuery(questionQueryDTO);
        }
        PaginationDTO paginationDTO = new PaginationDTO();
        for (Question question : questions) {
            System.out.println(question.getId()+" "+question.getDescription());
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        //问题的个数
        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }

    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();

        Integer totalPage;
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        //问题的个数
        Integer totalCount = (int) questionMapper.countByExample(questionExample);

        if (totalCount % size == 0){
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if(page < 1)page = 1;
        if(page > totalPage) page = totalPage;

        Integer offset = size * (page-1);
        System.out.println("userId " + userId + "offset " + offset + "size " + size);
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example,new RowBounds(offset,size));

        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);

        paginationDTO.setPagination(totalCount,page,size);
        return paginationDTO;
    }

    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        questionDTO.setCreator(question.getCreator());
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
//        System.out.println(questionDTO);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId() == null){
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        } else {
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if(updated != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Integer id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if(StringUtils.isBlank(queryDTO.getTag())){
            return new ArrayList<>();
        }
        String tag = queryDTO.getTag();
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(tag);
        List<Question> questions = questionExtMapper.selectRelated(question);
         List<QuestionDTO> questionDTOS = questions.stream().map(q->{
             QuestionDTO questionDTO = new QuestionDTO();
             BeanUtils.copyProperties(q,questionDTO);
             return  questionDTO;
         }).collect(Collectors.toList());
        return questionDTOS;
    }
}
