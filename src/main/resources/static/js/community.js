/*
* 提交回复*/
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment-content").val();
    comment2Target(questionId,1,content);
}
function dateForm(time){
    var unixTimestamp = new Date(time);
    return unixTimestamp.toLocaleString();
};
Date.prototype.toLocaleString = function() {
    return this.getFullYear() + "-" + (this.getMonth() + 1) + "-" + this.getDate();
};
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);
    var collapse = e.getAttribute("data-collapse");
    if(collapse){
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else{
        var subCommentContainer = $("#comment-"+id);

        if(subCommentContainer.children().length != 1){
            comments.addClass("in");
            e.setAttribute("data-collapse","in");
            e.classList.add("active");
        }
        else{
            $.getJSON("/comment/"+id,function (data){
                $.each(data.data,function (index,comment) {

                    var mediaLeftElement = $("<div/>",{
                        "class":"media-left"
                    }).append($("<img/>",{
                        "class":"media-object img-rounded",
                        "src":comment.user.avatarUrl
                    }));
                    var mediaBodyElement = $("<div/>",{
                        "class":"media-body"
                    }).append($("<h5/>",{
                        "class":"media-heading",
                        "html":comment.user.name}))
                        .append($("<div/>",{
                        "html":comment.content}))
                        .append($("<div/>",{
                        "class":"menu",}))
                        .append($("<span/>",{
                        "class":"pull-right menu",
                        "html":dateForm(comment.gmtCreate)}));


                    var mediaElement = $("<div/>",{
                       "class":"media"
                    }).append(mediaLeftElement)
                        .append(mediaBodyElement);

                    var commentElement = $("<div/>",{
                        "class":"col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);
                    subCommentContainer.prepend(commentElement);
                });
                //展开二级评论
                comments.addClass("in");
                e.setAttribute("data-collapse","in");
                e.classList.add("active");
            });
        }
    }
}

function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-"+commentId).val();
    comment2Target(commentId,2,content);
}

function comment2Target(targetId,type,content) {
    if(!content){
        alert("输入不能为空");
        return;
    }
    $.ajax({
        type:"POST",
        url: "/comment",
        contentType:"application/json",
        data: JSON.stringify({
            "parentId":targetId,
            "content":content,
            "type":type
        }),
        success: function(response){
            if(response.code == 200 || response.message=="成功"){
                window.location.reload();
            }else{
                if(response.code == 2003){
                    var isAccepted = confirm(response.message);
                    if(isAccepted){
                        window.open("https://github.com/login/oauth/authorize?client_id=4d837fe2e17b6ee930c6&redirect_uri=http://localhost:8081/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", true);
                    }
                    else{
                        alert(response.message);
                    }
                }
            }
            console.log(response);
        } ,
        dataType: "json"
    });
}