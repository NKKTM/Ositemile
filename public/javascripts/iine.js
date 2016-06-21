var allowAjax = true;

// いいねボタン（ログインしているとき）
$(function(){
  $(".iineBtnLonined").click( function(){
    if(allowAjax) {
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

      //ajaxの処理
      var jsondata = {'iineBtn': $(this).val()};
      $.post("/iineBtn/"+postId,
        jsondata,
        function (result) {   
          $("#iineNum"+postId).html(result.iineNum);
          allowAjax = true;
        },
        "json"
      );
    }else{
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

// いいねの変更処理を行う関数
function iineChange(postId){


}