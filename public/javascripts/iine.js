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
        console.log("いいね："+result.iineBtn);
      },
      "json"
    );
    //valueによってスタイル変更
    var iine = document.getElementById('iineBtn');
    if(iine.value === "true"){
      iine.innerHTML = "☆"
    }else{
      iine.innerHTML = "★"
    }
  })
})