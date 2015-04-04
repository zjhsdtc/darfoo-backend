$(function () {
    var type = $("#type").text();

    $("#start").click(function () {
        $.ajax({
            type: "POST",
            url: "/darfoobackend/rest/resources/" + type + "/create",
            data: $("#createresourceform").serialize(),
            success: function (data) {
                if (data == "200") {
                    alert("提交资源信息成功");
                    window.location.href = "/darfoobackend/rest/resources/" + type + "/resource/new"
                } else if (data == "500") {
                    alert("资源名字与明星舞队的组合已经存在,请更换资源标题或者名字");
                } else if (data == "501") {
                    alert("相同名字的图片已经存在了,请修改图片名字");
                } else if (data == "502") {
                    alert("明星舞队还不存在");
                } else if (data == "503") {
                    alert("请保证资源首字母是单个英文字符");
                } else if (data == "504") {
                    alert("请保证图片名字带有后缀");
                } else if (data == "505") {
                    alert("伴奏名字和舞队名字组合已经存在,请修改伴奏名字");
                } else if (data == "506") {
                    alert("相同名字的明星舞队已经存在");
                } else if (data == "507") {
                    alert("资源名字不能为空");
                } else if (data == "508") {
                    alert("作者名字不能为空");
                } else if (data == "509") {
                    alert("作者简介不能为空");
                } else {
                    alert("提交资源信息失败");
                }
            },
            error: function () {
                alert("提交资源信息失败");
            }
        });
    });
});