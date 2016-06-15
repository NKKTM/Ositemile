// 初期化
window.onload = function() {
};

// いいねボタン（ログインしているとき）
$(function(){
  $("#iineBtn").click( function(){
    iineChange();
  })
})

// いいねボタン（ログインしていない時→新規登録へ遷移）
$(function(){
  $("#iineBtnNanashi").click( function(){
      window.location.href = '/login';
  })
})

// いいねの変更処理を行う関数
function iineChange(){

    //iineBtnをvalueによってスタイル変更,その後value反転
    var iine = document.getElementById('iineBtn');
    if(iine.value === "true"){
      iine.innerHTML = "☆";
      iine.value = "false";
    }else if(iine.value === "false"){
      iine.innerHTML = "★";
      iine.value = "true";      
    }

    //postIdを取得
    var url   = location.href;
    params    = url.split("/");
    var postId = params[4];

    //ajaxの処理
    var jsondata = {'iineBtn': $("#iineBtn").val()};
    $.post("/iineBtn/"+postId,
      jsondata,
      function (result) {
        var iineNum = document.getElementById('iineNum');
        iineNum.innerHTML = result.iineNum;
      },
      "json"
    );
}