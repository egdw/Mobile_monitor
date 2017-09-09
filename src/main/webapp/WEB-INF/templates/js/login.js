/**
 * Created by hdy on 2017/9/9.
 */
$("#loginBtn").click(function () {
    console.log("click")
    $.ajax({
        type: 'post',
        url: 'login',
        data: $("#loginForm").serialize(),
        success: function (data) {
            alert(data);
        },
        error: function (e) {
            alert("网络错误，请重试！！");
        }
    });
})

