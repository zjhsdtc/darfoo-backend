<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@include file="header.jsp"%>

<script>
    function start(){
        $.ajax({
            type : "POST",
            url : "/darfoobackend/rest/resources/team/create",
            data : $("#createteamform").serialize(),
            success : function(data){
                if(data == "200"){
                    alert("提交舞队信息成功");
                    window.location.href = "/darfoobackend/rest/resources/teamresource/new"
                }else if(data == "501"){
                    alert("相同名字的舞队已经存在了");
                }else if(data == "503"){
                    alert("相同名字的图片已经存在了");
                }else{
                    alert("提交舞队信息失败");
                }
            },
            error : function(){
                alert("提交舞队信息失败");
            }
        })
    }
</script>

<div class="container">
    <h1>提交舞队信息</h1>
    <div class="row">
        <div class="col-md-12">
            <form role="form" id="createteamform" name="createteamform">
                <div class="form-group">
                    <label for="name">舞队名字</label>
                    <input type="text" class="form-control" name="name" id="name" placeholder="请输入舞队名字">
                </div>
                <div class="form-group">
                    <label for="description">舞队简介</label>
                    <input type="text" class="form-control" name="description" id="description" placeholder="请输入舞队简介">
                </div>
                <div class="form-group">
                    <label for="imagekey">舞队图片标题(也就是上传图片文件的文件名,需要加上后缀)</label>
                    <input type="text" class="form-control" name="imagekey" id="imagekey" placeholder="请输入舞队图片名称">
                </div>
                <button type="button" class="btn btn-default" onclick="start()">提交舞队信息</button>
            </form>
        </div>
    </div>
</div>

<%@include file="footer.jsp"%>