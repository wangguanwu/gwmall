function secKill(){
    if ($("#verifyCodeText").val() == "") {
        console.log("请输入验证码！");
        showVerifyCode();
    } else {
        $.post("skcart/checkCode?verifyCode="+$("#verifyCodeText").val(),function (data) {
            console.log(data);
            if(data.code == 200){
                console.log("验证码通过，提交请求！");
                localStorage.setItem("productId", $("#productId").val());
                localStorage.setItem("flashPromotionId", $("#flashPromotionId").val());
                localStorage.setItem("memberId", $("#memberId").val());
                window.location.href = "secKillConfirmOrder.html";
            }else{
                alert("验证码不正确！");
                $("#verifyCodeText").val("");
            }
        })
    }
}

function showVerifyCode(){
    $("#verifyCodeImg").src="skcart/verifyCode";
    $("#verifyCodeArea").attr("hidden",false);
}

function getProductStock(){
    console.log("productId:" + $("#productId").val());
    console.log("flashPromotionId:" + $("#flashPromotionId").val());
    console.log("memberId:" + $("#memberId").val());
    $.get("cache/stock?productId="+$("#productId").val(),function (data) {
        console.log(data);
        if(data > 0){
            $("#secKillbtn").disabled=false;
            $("#flashPromotionCount").val(data);
        }else{
            console.log("秒杀商品已无库存，秒杀结束！");
            $("#secKillbtn").disabled=true;
        }
    })
}