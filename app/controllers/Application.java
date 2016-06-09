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
import models.service.*;
import models.amazon.*;




public class Application extends Controller {

    // 定数
    // AmazonAPIにリクエストするURLの固定部分
    private static final String AMAZON_URL = "https://app.rakuten.co.jp/services/api/IchibaItem/Search/20140222?applicationId=1084889951156254811&format=xml&sort=-reviewCount&keyword=";

    //楽天ジャンル検索APIにリクエストするURLの固定部分
    private static final String RAKUTEN_GENRE_URL = "https://app.rakuten.co.jp/services/api/IchibaGenre/Search/20140222?applicationId=1084889951156254811&format=xml&genreId=";

    public static Result index() {       
        return ok(index.render(session().get("loginId"),PostModelService.use().getPostList(1)));
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

    // ユーザーに管理者権限を付与する
    public static Result grantAdmin(Long id) {
        User user = UserModelService.use().getUserById(id);
        user.setAdmin(true);
        user.save();
        System.out.println(user.getUserName()+"に管理者権限を付与しました");
        return redirect("/");
    }

    // 管理者ログイン
    @Security.Authenticated(SecuredAdmin.class)   
    public static Result admin() {
        System.out.println("管理者ログインできてます。");
        return redirect("/");
    }



    // 投稿するアイテムを検索する
    public static Result postSearchItem() throws Exception{

        // アイテムを探すワードを取得
        String[] params = {"searchWord"};
        DynamicForm searchWord = Form.form();
        searchWord = searchWord.bindFromRequest(params);
        Form<Goods> goodsForm = Form.form(Goods.class);
        if(searchWord.data().get("searchWord") == null){
        	return ok(postSearchItem.render(session().get("loginId"),null,goodsForm));
        }
        String searchWordStr = searchWord.data().get("searchWord").toString();
        // URLと結合
        String searchUrl = AMAZON_URL + searchWordStr;
        System.out.println("searchUrl："+searchUrl);
        Element elementRoot = AmazonModelService.use().getElement(searchUrl);
        List<Goods> goodsList = AmazonModelService.use().getSearchedGoodsList(elementRoot);
        return ok(postSearchItem.render(session().get("loginId"),goodsList,goodsForm));
    }

    //投稿するアイテムを選択しコメントなどを投稿するフォーム画面に移動する
    public static Result postInput(){
    	Form<Goods> goodsForm = form(Goods.class).bindFromRequest();
    	Form<Post> postForm = Form.form(Post.class);
    	if( !goodsForm.hasErrors() ){
    		//フォームにエラーなし
    		System.out.println("エラーなし");
    		return ok(postInput.render(session().get("loginId"),goodsForm,postForm));
    	}else{
    		//フォームにエラーあり
    		System.out.println("エラーあり");
    		return ok(postSearchItem.render(session().get("loginId"),null,form(Goods.class)));
    	}
    }
    //postとgoodsをdbに保存
    public static Result postCreate() throws Exception{
    	Form<Goods> goodsForm = form(Goods.class).bindFromRequest();
    	Form<Post> postForm = form(Post.class).bindFromRequest();
    	if(!goodsForm.hasErrors()){
    		System.out.println("goodsフォームはエラーなし");

    		if(!postForm.hasErrors()){
    			//Goodsのフォームにも、postのフォームにもエラ-がない時
        		System.out.println("Goodsのフォームにも、postのフォームにもエラ-がない");
        		Goods item = new Goods(goodsForm.get().getGoodsName(),goodsForm.get().getImageUrl(),goodsForm.get().getAmazonUrl(),goodsForm.get().getGenreId());
        		Post post = new Post(postForm.get().getPostTitle(),postForm.get().getPostComment());
        		String genreSearchUrl = RAKUTEN_GENRE_URL + goodsForm.get().getGenreId();
        		Element elementRoot = AmazonModelService.use().getElement(genreSearchUrl);
        		List<String> categoryList = AmazonModelService.use().getCategoryList(elementRoot);
        		item.setCategory(categoryList);
        		post.setGoods(item);
        		item.setPost(post);
        		item.save();
        		post.save();
        		System.out.println(item.getCategory().get(0));
        		System.out.println(item.getCategory().get(1));
        	}else{
        		//エラー：postのフォームにのみエラ-がある時
        		System.out.println("postフォームにのみエラーあり！！");
        	}

    	}else{
    		//エラー：Goodsのフォームにエラーがある時
    		System.out.println("goodsフォームでエラーあり！！");
    	}


    	return TODO;
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
    	 String loginId = session().get("loginId");
    	// コメントデーターの参照
    	CommentModelService commnetService = CommentModelService.use();
    	List<Comment> commentList = commnetService.getCommnetList();
    	if( commentList != null ){
    		return ok(introduction.render(loginId,commentList ,commentForm));
    	}else{
    		return ok(introduction.render(loginId,null,commentForm));
    	}


    }

    // コメント登録
    public static Result commentCreate(){
        Form<CommentForm> commnetForm = form(CommentForm.class).bindFromRequest();
        if( !commnetForm.hasErrors() ){
            // エラーがない
            String loginId = session().get("loginId");
            if(loginId == null){
                System.out.println("ログインするか、新規登録をお願いします。");
                return redirect(controllers.routes.Application.introduction());
            }

            // コメント登録
            commnetForm.get().comment = commnetForm.get().comment.replaceAll("\n","<br />");
            Comment comment = new Comment(commnetForm.get().comment,UserModelService.use().getUserByLoginId(loginId),null);
            CommentModelService.use().save(comment);

            return redirect(controllers.routes.Application.introduction());
        }else{
        }
        return null;
    }

}
