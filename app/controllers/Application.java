package controllers;

import play.*;

import play.mvc.*;

import play.data.Form;
import static play.data.Form.*;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.w3c.dom.*;
import org.w3c.dom.Element;


import javax.xml.parsers.DocumentBuilderFactory;

import play.data.DynamicForm;

import views.html.*;
import views.html.login.*;
import views.html.post.*;

import models.entity.*;
import models.entity.Comment;
import models.form.*;
import models.login.*;
import models.service.CommentModelService;
import models.service.UserModelService;
import models.amazon.*;




public class Application extends Controller {

    // 定数
    // AmazonAPIにリクエストするURLの固定部分
    private static final String AMAZON_URL = "https://app.rakuten.co.jp/services/api/IchibaItem/Search/20140222?applicationId=1084889951156254811&format=xml&sort=-reviewCount&keyword=";


    public static Result index() {
        String loginId = session().get("loginId");
        System.out.println("loginId:"+loginId);
        return ok(index.render(loginId));
    }

    //ログイン画面
    public static Result login() {
        Form<LoginForm> loginForm = new Form(LoginForm.class);
        return ok(login.render(loginForm));
    }

    //新規登録画面
    public static Result register() {
        Form<RegisterForm> registerForm = new Form(RegisterForm.class);
        return ok(register.render(registerForm));
    }

    //新規ユーザーをDBに登録
    public static Result registerDB(){
        Form<RegisterForm> registerForm = form(RegisterForm.class).bindFromRequest();
        if(!registerForm.hasErrors()){
        	//ユーザー情報がフォームから取得できた場合
            //DBにuserを登録
        	User user = new User();
            user.setUserName(registerForm.get().userName);
            user.setPassword(registerForm.get().password);
            user.setLoginId(registerForm.get().loginId);
            user.save();
            System.out.println("DB登録に成功しました！");
            //セッションにloginIdを登録
            session().clear();
            session("loginId", user.getLoiginId());
            return redirect("/");
        }else{
        	//ユーザー情報がフォームから取得できなかった場合
            System.out.println("DB登録に失敗しました！");
            return redirect("/");
        }
    }

    // ログイン認証
    public static Result authenticate() {
        Form<LoginForm> loginForm = form(LoginForm.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            //ログイン入力がフォームから取得できなかった場合
            return badRequest(login.render(loginForm));
        } else {
            //ログイン入力がフォームから取得できた場合
            session().clear();
            session("loginId", loginForm.get().loginId);
            String returnUrl = ctx().session().get("returnUrl");
            if(returnUrl == null || returnUrl.equals("") || returnUrl.equals(routes.Application.login().absoluteURL(request()))) {
                returnUrl = routes.Application.authenticateSuccess().absoluteURL(request());
            }
            return redirect(returnUrl);
        }
    }

    // 認証成功したらここへ
    @Security.Authenticated(Secured.class)
    public static Result authenticateSuccess() {
            System.out.println("認証成功");
            return redirect("/");
    }

    // ログアウト
    @Security.Authenticated(Secured.class)
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect("/");
    }

    // POSTするアイテムを検索する
    public static Result postSearchItem() throws Exception{
        // アイテムを探すワードを取得
        String[] params = {"searchWord"};
        DynamicForm searchWord = Form.form();
        searchWord = searchWord.bindFromRequest(params);
        if(searchWord.data().get("searchWord") == null){
        	return ok(postSearchItem.render(session().get("loginId"),null));
        }
        String searchWordStr = searchWord.data().get("searchWord").toString();
        // URLと結合
        String searchUrl = AMAZON_URL + searchWordStr;
        System.out.println("searchUrl："+searchUrl);
        Element elementRoot = AmazonModelService.use().getElement(searchUrl);
        List<Goods> goodsList = AmazonModelService.use().getSearchedGoodsList(elementRoot);
        return ok(postSearchItem.render(session().get("loginId"),goodsList));
    }

    //ユーザーページ *中の処理未実装
    public static Result userPage(){
    	 String loginId = session().get("loginId");
         System.out.println("loginId:"+loginId);
         return ok(user_page.render(loginId));
    }

    // 商品ページ
    public static Result introduction(){
    	Form<CommentForm> commentForm = new Form(CommentForm.class);

    	// コメントデーターの参照
    	CommentModelService commnetService = CommentModelService.use();
    	List<Comment> commentList = commnetService.getCommnetList();
    	if( commentList != null ){
    		return ok(introduction.render(commentList ,commentForm));
    	}else{
    		return ok(introduction.render( null,commentForm));
    	}


    }

    // コメント登録
    public static Result commentCreate(){
        Form<CommentForm> commnetForm = form(CommentForm.class).bindFromRequest();
        if( !commnetForm.hasErrors() ){
            // エラーがない
            // コメント登録
            Comment comment = new Comment(commnetForm.get().comment,null,null);
            CommentModelService.use().save(comment);

            return redirect(controllers.routes.Application.introduction());
        }else{
        }
        return null;
    }

}
