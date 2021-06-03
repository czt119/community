package com.example.servingwebcontent.service;

import com.example.servingwebcontent.dto.CommentDTO;
import com.example.servingwebcontent.enums.CommentTypeEnum;
import com.example.servingwebcontent.enums.NotificationStatusEnum;
import com.example.servingwebcontent.enums.NotificationTypeEnum;
import com.example.servingwebcontent.exception.CustomizeErrorCode;
import com.example.servingwebcontent.exception.CustomizeException;
import com.example.servingwebcontent.mapper.*;
import com.example.servingwebcontent.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional
    public void insert(Comment comment, User commentator) {
        if(comment.getParentId() == null || comment.getParentId() == 0){
            throw  new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }

        if(comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
            throw  new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if(comment.getType() == CommentTypeEnum.COMMENT.getType()){
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment == null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            // 回复问题
            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if(question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            //创建通知
            createNotify(comment,dbComment.getCommentator(),commentator.getName(),question.getTitle(),NotificationTypeEnum.REPLY_COMMENT.getType(),question.getId());

        } else {
            // 回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
            //创建通知
            createNotify(comment,question.getCreator(),commentator.getName(),question.getTitle(),NotificationTypeEnum.REPLY_QUESTION.getType(),question.getId());
        }
    }

    private void createNotify(Comment comment,Integer creatorId,String notifierName,String outTitle,Integer type,Integer outerId) {
        if(comment.getCommentator() == creatorId){
            return ;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(type);
        notification.setOuterId(outerId);
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(creatorId);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outTitle);
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByTargetId(Integer id,CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        System.out.println("comments:"+comments);
        if(comments.size()==0){
            return new ArrayList<>();
        }
        //回去评论者
        Set<Integer> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Integer> userIds = new ArrayList();
        userIds.addAll(commentators);
        //创建评论与评论者的映射
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Integer,User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
        //把comment转换成commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}
