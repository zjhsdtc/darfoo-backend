<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../header.jsp" %>

<div class="container">
    <h1>上传越剧视频资源</h1>

    <div class="row">
        <div class="col-md-12">
            <form role="form" action="/darfoobackend/rest/resources/operavideo/videoimage/create" method="post"
                  enctype="multipart/form-data">
                <div class="form-group">
                    <label for="videoresource">选择要上传的视频资源(请确保是MP4或者FLV格式的视频文件)</label>
                    <input type="file" id="videoresource" name="videoresource">
                </div>

                <div class="form-group">
                    <label for="imageresource">选择要上传的视频封面图片(请确保是JPG或者PNG格式的图片文件)</label>
                    <input type="file" id="imageresource" name="imageresource">
                </div>

                <button type="submit" class="btn btn-default">上传越剧视频资源</button>
            </form>
        </div>
    </div>
</div>

<%@include file="../footer.jsp" %>