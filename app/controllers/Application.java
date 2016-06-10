package controllers;

import play.*;


import play.mvc.*;

import play.data.Form;
import static play.data.Form.*;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.List;
import java.util.Collections;

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
import models.form.admin.AdminCommentForm;
import models.login.*;
import models.service.*;
import models.amazon.*;

import views.html.admin.*;



public class Application extends Controller {

    // 定数
    // AmazonAPIにリクエストするURLの固定部分
    //private static final String AMAZON_URL = "https://app.rakuten.co.jp/services/api/IchibaItem/Search/20140222?applicationId=1084889951156254811&format=xml&sort=-reviewCount&keyword=";
	//ソート:standard
	private static final String AMAZON_URL = "https://app.rakuten.co.jp/services/api/IchibaItem/Search/20140222?applicationId=1084889951156254811&format=xml&sort=standard&keyword=";

    //楽天ジャンル検索APIにリクエストするURLの固定部分
    private static final String RAKUTEN_GENRE_URL = "https://app.rakuten.co.jp/services/api/IchibaGenre/Search/20140222?applicationId=1084889951156254811&format=xml&genreId=";



    public static Result index(Integer page) {       
        return ok(index.render(session().get("loginId"),PostModelService.use().getPostList(page),GoodsModelService.use().getGoodsAllCategory(),page,PostModelService.use().getMaxPage()));
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
            session("loginId", user.getLoginId());
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
    @Security.Authenticated(Secured.class)
    public static Result postSearchItem() throws Exception{
    	Form<SearchItemForm> searchForm = form(SearchItemForm.class).bindFromRequest();
        // アイテムを探すワードを取得
//        String[] params = {"searchWord"};
//        DynamicForm searchWord = Form.form();
//        searchWord = searchWord.bindFromRequest(params);
        Form<Goods> goodsForm = Form.form(Goods.class);
        if(searchForm.hasErrors()){
        	System.out.println("バインドエラーあり！！！");
        	return ok(postSearchItem.render(session().get("loginId"),null,goodsForm,searchForm));
        }else{
        	System.out.println("バインドエラーなし");
	        String searchWordStr = searchForm.get().searchWord;
	        System.out.println("searchWordStr:"+searchWordStr);
	        // URLと結合
	        String searchUrl = AMAZON_URL + searchWordStr;
	//        System.out.println("searchUrl："+searchUrl);
	        Element elementRoot = AmazonModelService.use().getElement(searchUrl);
	        List<Goods> goodsList = AmazonModelService.use().getSearchedGoodsList(elementRoot);
	        return ok(postSearchItem.render(session().get("loginId"),goodsList,goodsForm,searchForm));
	    }
    }

    //投稿するアイテムを選択しコメントなどを投稿するフォーム画面に移動する
    @Security.Authenticated(Secured.class)
    public static Result postInput(){
    	Form<Goods> goodsForm = form(Goods.class).bindFromRequest();
    	Form<Post> postForm = Form.form(Post.class);
    	if( !goodsForm.hasErrors() ){
    		//フォームにエラーなし
    		System.out.println("エラーなし");
    		Goods item = new Goods();
    		item.setGoodsName(goodsForm.get().getGoodsName());
    		item.setImageUrl(goodsForm.get().getImageUrl());
    		item.setAmazonUrl(goodsForm.get().getAmazonUrl());
    		item.setGenreId(goodsForm.get().getGenreId());
    		return ok(postInput.render(session().get("loginId"),goodsForm,postForm,item));
    	}else{
    		//フォームにエラーあり
    		System.out.println("エラーあり");
    		return ok(postSearchItem.render(session().get("loginId"),null,form(Goods.class),new Form<>(SearchItemForm.class)));
    	}
    }
    //postとgoodsをdbに保存
    @Security.Authenticated(Secured.class)
    public static Result postCreate() throws Exception{
    	Form<Goods> goodsForm = form(Goods.class).bindFromRequest();
    	Form<Post> postForm = form(Post.class).bindFromRequest();
    	if(!goodsForm.hasErrors()){
    		System.out.println("goodsフォームはエラーなし");

    		if(!postForm.hasErrors()){
    			//Goodsのフォームにも、postのフォームにもエラ-がない時
        		System.out.println("Goodsのフォームにも、postのフォームにもエラ-がない");
        		Goods item = new Goods(goodsForm.get().getGoodsName(),goodsForm.get().getImageUrl(),goodsForm.get().getAmazonUrl(),goodsForm.get().getGenreId());
        		String postComment = postForm.get().getPostComment();
        		postComment = postComment.replaceAll("\n", "<br>");
        		Post post = new Post(postForm.get().getPostTitle(),postComment);
        		String genreSearchUrl = RAKUTEN_GENRE_URL + goodsForm.get().getGenreId();
        		Element elementRoot = AmazonModelService.use().getElement(genreSearchUrl);
        		String category = AmazonModelService.use().getCategory(elementRoot);

                String loginId = session().get("loginId");
                User user = UserModelService.use().getUserByLoginId(loginId);

        		item.setCategory(category);
        		post.setGoods(item);
                post.setUser(user);
                post.setDateStr(PostModelService.use().getDateString());
        		item.setPost(post);
        		item.save();
        		post.save();

        	}else{
        		//エラー：postのフォームにのみエラ-がある時
        		System.out.println("postフォームにのみエラーあり！！");
        	}

    	}else{
    		//エラー：Goodsのフォームにエラーがある時
    		System.out.println("goodsフォームでエラーあり！！");
    	}
    	return redirect("/");
    }

    //ユーザーページ
    public static Result userPage(Long formatedUserId){
    	 Long userId = formatedUserId-932108L;
    	 User user = UserModelService.use().getUserById(userId);
    	 List<Post> postList = UserModelService.use().getPostByUserId(userId);
    	 int postListSize = 0;
    	 if(postList!=null){
    		 postListSize = postList.size();
    	 }
    	 String loginId = session().get("loginId");
         System.out.println("loginId:"+loginId);
         return ok(user_page.render(loginId,user,postList,postListSize));
    }

    // 商品ページ
    public static Result introduction(Long postId){
    	Form<CommentForm> commentForm = new Form(CommentForm.class);
    	String loginId = session().get("loginId");
    	// ポストの参照
        Post post = PostModelService.use().getPostListById(postId);
    	if( post.getComment() != null ){
            Collections.reverse(post.getComment());            
    		return ok(introduction.render(loginId,post,commentForm));
    	}else{
    		return ok(introduction.render(loginId,null,commentForm));
    	}


    }

    // コメント登録

    public static Result commentCreate(){
        // sessionからloginId取得
        String loginId = session().get("loginId");  
        // postIdからpostを取得
        String[] params = { "postId" };
        DynamicForm input = Form.form();
        input = input.bindFromRequest(params);
        Long postId = Long.parseLong(input.data().get("postId"));
        Post post = PostModelService.use().getPostListById(postId);
        // commentform取得
        Form<CommentForm> commentForm = form(CommentForm.class).bindFromRequest();
                   
        if( !commentForm.hasErrors() ){

            // エラーがない
        	System.out.println("入りました！！！");
            if(loginId == null){
                System.out.println("ログインするか、新規登録をお願いします。");
                return redirect(controllers.routes.Application.login());
            }
            // コメント登録

            commnetForm.get().comment = commnetForm.get().comment.replaceAll("\n","<br />");
            Comment comment = new Comment(commnetForm.get().comment,UserModelService.use().getUserByLoginId(loginId),post);
            comment.setDateStr(PostModelService.use().getDateString());
            CommentModelService.use().save(comment);

            return redirect(controllers.routes.Application.introduction(comment.getPost().getId()));
        }else{
            // 入力にエラーがあった場合
            Collections.reverse(post.getComment());                        
            return ok(introduction.render(loginId,post,commentForm));
        }
    }

    //ユーザー情報編集のフォーム画面に遷移する
    public static Result updateUserForm(Long formatedUserId){
    	String loginId = session().get("loginId");
    	Long userId = formatedUserId-932108L;
    	User user = UserModelService.use().getUserById(userId);
    	Form<User> userForm = form(User.class).fill(user);

    	return ok(update_user.render(loginId,userForm,user));
    }

    //ユーザー情報の編集を実行する
    public static Result DoUpdate(){
    	return TODO;
    }

}
