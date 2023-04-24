<!DOCTYPE html>
<!-- saved from url=(0040)http://localhost:8081/#/secKillDetail/26 -->
<html lang="en">
  
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width,initial-scale=1.0">
    <title>图灵商城商品详情页</title>
    <link rel="stylesheet" type="text/css" href="static/product.css">
  </head>

  <body>
    <noscript>
      <strong>We're sorry but mall doesn't work properly without JavaScript enabled. Please enable it to continue.</strong></noscript>
    <div id="app">
      <div>
        <div class="header">
          <div class="nav-topbar">
            <div class="container">
              <div class="topbar-menu">
                <a href="javascript:;">图灵商城</a></div>
              <div class="topbar-user">
                <a href="javascript:;">admin</a>
                <!---->
                <a href="javascript:;">退出</a>
                <a href="http://localhost:8081/#/order/list">我的订单</a>
                <a href="javascript:;" class="my-cart">
                  <span class="icon-cart"></span>购物车</a>
              </div>
            </div>
          </div>
          <div class="nav-header">
            <div class="container">
              <div class="header-logo">
                <a href="http://localhost:8081/#/index"></a>
              </div>
              <div class="header-search">
                <div class="wrapper">
                  <input type="text" name="keyword" placeholder="请输入产品关键字">
                  <a href="javascript:;" class="fa fa-search fa-2x"></a>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="detail">
          <div class="nav-bar">
            <div class="container">
              <div class="pro-title">${fpp.name}</div>
              <div class="pro-param">
                <a href="javascript:;">概述</a>
                <span>|</span>
                <a href="javascript:;">参数</a>
                <span>|</span>
                <a href="javascript:;">用户评价</a></div>
            </div>
          </div>
          <div class="wrapper">
            <div class="container clearfix">
              <div class="swiper">
                <div class="swiper-container swiper-container-initialized swiper-container-horizontal">
                  <div class="swiper-wrapper" style="transform: translate3d(-1284px, 0px, 0px); transition-duration: 0ms;">
                    <#list imageList as image>
                    <img src="${image}"/><br>
                  </#list>
                  </div>
                  <div class="swiper-pagination swiper-pagination-clickable swiper-pagination-bullets">
                    <span class="swiper-pagination-bullet" tabindex="0" role="button" aria-label="Go to slide 1"></span>
                    <span class="swiper-pagination-bullet" tabindex="0" role="button" aria-label="Go to slide 2"></span>
                    <span class="swiper-pagination-bullet swiper-pagination-bullet-active" tabindex="0" role="button" aria-label="Go to slide 3"></span>
                  </div>
                  <span class="swiper-notification" aria-live="assertive" aria-atomic="true"></span>
                </div>
              </div>
              <div class="content">
                <h2 class="item-title">${fpp.name}</h2>
                <p class="item-info">AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待</p>
                <div class="item-price">${fpp.flashPromotionPrice}元</div>
                <div class="line"></div>
                <div class="item-version clearfix">
                  <h2>选择规格</h2>
                  <div skuid="143" class="phone fl">金色16G</div>
                  <div skuid="144" class="phone fl">金色32G</div>
                  <div skuid="145" class="phone fl">银色16G</div>
                  <div skuid="146" class="phone fl">银色32G</div></div>
                <div class="item-total">
                  <div class="phone-info clearfix">
                    <div class="stock">
                      <i aria-hidden="true" class="fa fa-exclamation-triangle f2"></i>请选择规格</div>
                    <!----></div>
                  <!----></div>
                <div class="btn-group">
                  <a href="javascript:;" class="btn btn-huge fl" onclick="secKill()" disabled="true" id="secKillbtn">立即秒杀</a></div>
                <div class="btn-group">
                  <span>
                    <em class="item-price">库存数量</em>
                  </span>
                  <span>
                    <input id="flashPromotionCount" type="text" readonly value="${fpp.flashPromotionCount}">
                  </span>
                </div>
                <div class="input" id="verifyCodeArea" hidden="true">
                  <img id="verifyCodeImg" src="">
                  <input id="verifyCodeText" type="text" placeholder="请输入验证码">
                </div>
                <div class="after-sale-info">
                  <span>
                    <a href="javascript:void(0);" class="support">
                      <i aria-hidden="true" class="fa fa-check-circle-o f2"></i>
                      <em>无忧退货</em></a>
                  </span>
                  <span>
                    <a href="javascript:void(0);" class="support">
                      <i aria-hidden="true" class="fa fa-check-circle-o f2"></i>
                      <em>快速退款</em></a>
                  </span>
                  <span>
                    <a href="javascript:void(0);" class="support">
                      <i aria-hidden="true" class="fa fa-check-circle-o f2"></i>
                      <em>免费包邮</em></a>
                  </span>
                </div>
              </div>
            </div>
          </div>
          <div class="price-info">
            <div class="container">
              <h2>商品介绍</h2>
              <div class="desc">
                <p>
					<!--{* detailHtml *}-->
                </p>
              </div>
            </div>
          </div>
          <div class="service">
            <div class="container">
              <ul>
                <li>
                  <span class="icon-setting"></span>预约维修服务</li>
                <li>
                  <span class="icon-7day"></span>7天无理由退货</li>
                <li>
                  <span class="icon-15day"></span>15天免费换货</li>
                <li>
                  <span class="icon-post"></span>满150元包邮</li>
              </ul>
            </div>
          </div>
        </div>
        <div data-v-b07fc4c4="" class="footer">
          <div data-v-b07fc4c4="" class="footer-logo">
            <p data-v-b07fc4c4="">图灵商城</p></div>
          <div data-v-b07fc4c4="" class="footer-link">
            <a data-v-b07fc4c4="" href="http://www.tulingxueyuan.cn/" target="_blank">图灵学院</a>
            <span data-v-b07fc4c4="">|</span>
            <a data-v-b07fc4c4="" href="https://ke.qq.com/course/231516?tuin=a6505b53" target="_blank">腾讯课堂java架构师培训</a>
            <span data-v-b07fc4c4="">|</span>
            <a data-v-b07fc4c4="" href="https://ke.qq.com/course/429988" target="_blank">数据结构与算法</a>
            <span data-v-b07fc4c4="">|</span>
            <a data-v-b07fc4c4="" href="https://tuling.ke.qq.com/" target="_blank">腾讯课堂图灵学院</a></div>
          <div data-v-b07fc4c4="" class="copyright">Copyright ©2019
            <span data-v-b07fc4c4="" class="domain">图灵学院</span>All Rights Reserved.</div></div>
      </div>
      <input id="memberId" type="hidden" value="{* memberId *}">
      <input id="productId" type="hidden" value="{* productId *}">
      <input id="flashPromotionId" type="hidden" value="{* flashPromotionId *}">
    </div>

    <script type="text/javascript" src="static/jquery-1.9.1.min.js"></script>
    <script type="text/javascript">
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
              window.location.href = "static/secKillConfirmOrder.html";
            }else{
              alert("验证码不正确！");
              $("#verifyCodeText").val("");
            }
          })
        }
      }

      function showVerifyCode(){
        $("#verifyCodeImg").attr("src","skcart/verifyCode");
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

      getProductStock();
    </script>
  </body>

</html>