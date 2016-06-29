// Ajaxの処理を行うかを決めるフラグ
var allowAjax = true;

// いいねボタン（ログインしているとき）
$(function(){
  $(".iineBtnLonined").click( function(){
    if(allowAjax) {
    // Ajaxが処理中でないとき
      allowAjax = false;        
      //postIdを取得
      var postId = $(this).next().val();
      console.log("postIdは"+postId);  

      //iineBtnをvalueによってスタイル変更,その後value反転
      if(this.value === "true"){
        this.innerHTML = "☆";
        this.value = "false";
      }else if(this.value === "false"){
        this.innerHTML = "★";
        this.value = "true";      
      }

      //ajaxの処理（連続して押されないために処理間隔を0.1秒あける）
      var jsondata = {'iineBtn': $(this).val()};
      $.post("/iineBtn/"+postId,
        jsondata,
        function (result) {   
          setTimeout(function() {               
          $("#iineNum"+postId).html(result.iineNum);
          allowAjax = true;
          },100);        
        },
        "json"
      );
    }else{
    // Ajaxが処理中の場合
      console.log("処理中です");
    }
  })
})

// いいねボタン（ログインしていない時→新規登録へ遷移）
$(function(){
  $(".iineBtnNanashi").click( function(){
      window.location.href = '/login';
  })
})
