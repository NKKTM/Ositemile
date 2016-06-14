// いいねボタン（ログインしているとき）
$(function(){
  $("#iineBtn").click( function(){
    //postIdを取得
    var url   = location.href;
    params    = url.split("/");
    var postId = params[4];

    //ajaxの処理
    var jsondata = {'iineBtn': $("#iineBtn").val()};
    $.post("/iineBtn/"+postId,
      jsondata,
      function (result) {
        $("#iineBtn").val(result.iineBtn);
        var iineNum = document.getElementById('iineNum');
        iineNum.innerHTML = result.iineNum;
      },
      "json"
    );

    //iineBtnをvalueによってスタイル変更
    var iine = document.getElementById('iineBtn');
    if(iine.value === "true"){
      iine.innerHTML = "☆"
    }else if(iine.value === "false"){
      iine.innerHTML = "★"
    }
  })
})

// いいねボタン（ログインしていない時→新規登録へ遷移）
$(function(){
  $("#iineBtnNanashi").click( function(){
      window.location.href = '/login';
  })
})