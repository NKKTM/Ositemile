package controllers;

import play.*;


import play.mvc.*;

import play.data.Form;
import play.db.ebean.Model.Finder;

import static play.data.Form.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.List;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

import org.w3c.dom.*;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;


import play.libs.Json;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.xml.parsers.DocumentBuilderFactory;

import play.data.DynamicForm;

import views.html.*;
import views.html.login.*;
import views.html.post.*;
import views.html.iine.*;


import models.entity.*;
import models.entity.Comment;
import models.form.*;
import models.form.admin.AdminCommentForm;
import models.login.*;
import models.service.*;
import models.MakeImage;
import models.amazon.*;

import views.html.admin.*;

import play.mvc.Http.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

import org.apache.commons.lang3.StringUtils;



public class Application extends Controller {

    // 定数
    // AmazonAPIにリクエストするURLの固定部分
    //private static final String AMAZON_URL = "https://app.rakuten.co.jp/services/api/IchibaItem/Search/20140222?applicationId=1084889951156254811&format=xml&sort=-reviewCount&keyword=";
	//ソート:standard
	private static final String AMAZON_URL = "https://app.rakuten.co.jp/services/api/IchibaItem/Search/20140222?applicationId=1084889951156254811&format=xml&sort=standard&keyword=";

    //楽天ジャンル検索APIにリクエストするURLの固定部分
    private static final String RAKUTEN_GENRE_URL = "https://app.rakuten.co.jp/services/api/IchibaGenre/Search/20140222?applicationId=1084889951156254811&format=xml&genreId=";


    public static Result index(Integer page,String category) {
        // ポストのリスト取得
        List<Post> postList;
        if(category.equals("ALL")){
            postList = PostModelService.use().getPostList(page);
        }else{
            postList = PostModelService.use().getPostListByCategory(page,category);
        }
        // セッションId取得
        String loginId = session().get("loginId");

        // いいねが押されているかの判定
        List<Boolean> booleanList = IineModelService.use().getBooleanListByPostList(postList,loginId);

        return ok(index.render(loginId,postList,booleanList,GoodsModelService.use().getGoodsAllCategory(),page,PostModelService.use().getMaxPage(category),category,Form.form(models.form.SearchPostForm.class)));
    }

    //ログイン画面
    public static Result login() {
        Form<LoginForm> loginForm = new Form(LoginForm.class);
        return ok(login.render(loginForm));
    }

    //新規登録画面
    public static Result register() {
        Form<RegisterForm> registerForm = new Form(RegisterForm.class);
        return ok(register.render(registerForm,""));
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
            if(UserModelService.use().checkLoginId(registerForm.get().loginId)){
            	//id重複チェック：重複している場合
            	return ok(register.render(registerForm,"このIDはすでに使われています。"));
            }
            user.setLoginId(registerForm.get().loginId);
            File file = new File("public/images/unknown.png");
            try {
				user.setImageData(MakeImage.getBytesFromImage(ImageIO.read(file), "png"));
                user.setImageEncData(Base64.getEncoder().encodeToString(user.getImageData()));
				user.setImageName("unknown.png");
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
            user.save();
            System.out.println("DB登録に成功しました！");
            //セッションにloginIdを登録
            session().clear();
            session("loginId", user.getLoginId());
            return redirect("/");
        }else{
        	//ユーザー情報がフォームから取得できなかった場合
            System.out.println("DB登録に失敗しました！");
            return ok(register.render(registerForm,""));
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
        return redirect("/postList");
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
        	return ok(postSearchItem.render(session().get("loginId"),null,goodsForm,searchForm));
        }else{
        	if(StringUtils.isBlank(searchForm.get().searchWord)){
        		return ok(postSearchItem.render(session().get("loginId"),null,goodsForm,searchForm));
        	}
	        String searchWordStr = searchForm.get().searchWord;
	        searchWordStr = URLEncoder.encode(searchWordStr,"utf-8");
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
        		if(StringUtils.isBlank(postForm.get().getPostTitle()) || StringUtils.isBlank(postForm.get().getPostComment())){
    				return ok(postInput.render(session().get("loginId"),goodsForm,postForm,item));
    			}
        		String postComment = postForm.get().getPostComment();
        		postComment = PostModelService.use().sanitizeString(postComment);
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
        		Goods item = new Goods(goodsForm.get().getGoodsName(),goodsForm.get().getImageUrl(),goodsForm.get().getAmazonUrl(),goodsForm.get().getGenreId());
        		return ok(postInput.render(session().get("loginId"),goodsForm,postForm,item));
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
    	 Collections.reverse(postList);
         List<Iine> iineList = IineModelService.use().getIineListByUserId(userId);
    	 String loginId = session().get("loginId");
         // いいねが押されているかの判定（postに対して）
         List<Boolean> postBooleanList = IineModelService.use().getBooleanListByPostList(postList,loginId);
         // いいねが押されているかの判定（iineに対して）
         List<Boolean> iineBooleanList = IineModelService.use().getBooleanListByIineList(iineList,loginId);
         return ok(user_page.render(loginId,user,postList,iineList,postBooleanList,iineBooleanList));
    }

    //loginIdからユーザーページのリンクを作る
    public static Result getUserPageByLoginid(String loginId){
    	User user = UserModelService.use().getUserByLoginId(loginId);
    	Long formatedUserId = user.getId() + 932108L;
    	return redirect(controllers.routes.Application.userPage(formatedUserId));
    }

    // 商品ページ
    public static Result introduction(Long postId){
    	Form<CommentForm> commentForm = new Form(CommentForm.class);
    	String loginId = session().get("loginId");

        // いいねが押されているかの判定
        User user = UserModelService.use().getUserByLoginId(loginId);
        boolean iine = false;
        if(user != null){
            //ログインしている時のみ実行
            if(IineModelService.use().getIineById(postId,user.getId()) != null){
                //いいねがすでに押されている場合はtrueにする
                iine = true;
            }
        }

    	// ポストの参照
        Post post = PostModelService.use().getPostListById(postId);
    	if( post.getComment() != null ){
    		// コメント情報取得
    		List<Comment> comment = CommentModelService.use().getCommentList(postId);
            Collections.reverse(comment);
            List<Iine> iineList = IineModelService.use().getIineListByPostId(postId);
    		return ok(introduction.render(loginId,post,commentForm,comment,iine,iineList));
    	}else{
    		return ok(introduction.render(loginId,null,commentForm,null,null,null));
    	}
    }

    // いいねJSONデータの作成
    @Security.Authenticated(Secured.class)
    public static Result iineBtn(Long postId) {
        String iineBtn = request().body().asFormUrlEncoded().get("iineBtn")[0];
        ObjectNode result = Json.newObject();
        //loginIdを取得
        String loginId = session().get("loginId");
        //ユーザーとPOSTを取得
        User user = UserModelService.use().getUserByLoginId(loginId);
        Post post = PostModelService.use().getPostListById(postId);

        if (iineBtn != null) {
            // いいねボタンの値が取得できた時
            if(iineBtn.equals("true")){
                // いいねボタンの値がtrueのとき（いいね保存）
                result.put("iineBtn", "true");
                if(IineModelService.use().getIineById(post.getId(),user.getId()) == null){
                    //すでにこのpostIdとuserIdの組み合わせで登録されていなければセーブ
                    Iine iine = new Iine(post,user);
                    iine.save();
                }
            }else if(iineBtn.equals("false")){
                // いいねボタンの値がfalseのとき（いいね削除）
                result.put("iineBtn", "false");
                Iine iine = IineModelService.use().getIineById(post.getId(),user.getId());
                iine.delete();
            }
            //いいね数を格納
            int iineNum = IineModelService.use().getIineListByPostId(postId).size();
            // 投稿にいいね数を格納
            post.setIineCnt(iineNum);
            post.save();
            result.put("iineNum",iineNum);
            return ok(result);
        } else {
        // いいねボタンの値が取得できなかった時
            result.put("iineBtn", "エラー");
            return badRequest(result);
        }
    }

    // いいねリスト
    public static Result iineListForPost(Long postId) {
        List<Iine> iineList = IineModelService.use().getIineListByPostId(postId);
        String loginId = session().get("loginId");
        return ok(iineListForPost.render(loginId,iineList));
    }

    // コメント登録
    @Security.Authenticated(Secured.class)
    public static Result commentCreate() throws ParseException{
        // sessionからloginId,ユーザーを取得
        String loginId = session().get("loginId");
        User user = UserModelService.use().getUserByLoginId(loginId);
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
            commentForm.get().comment = PostModelService.use().sanitizeString(commentForm.get().comment);
            Comment comment = new Comment(commentForm.get().comment,UserModelService.use().getUserByLoginId(loginId),post);
            comment.setDateStr(PostModelService.use().getDateString());
            CommentModelService.use().save(comment);
            System.out.println("こめんんんんと："+comment.getComment());

            // postにコメント情報を格納
            Post updatePost = PostModelService.use().getPostListById(postId);
            updatePost.getComment().add(comment);
            updatePost.setCommentCnt(PostModelService.use().getPostListById(postId).getComment().size());	// コメント数の保存
            updatePost.update();

            return redirect(controllers.routes.Application.introduction(comment.getPost().getId()));
        }else{
            // 入力にエラーがあった場合
            List<Comment> comment = CommentModelService.use().getCommentList(postId);
            Collections.reverse(comment);
            //いいねの判定
            boolean iine = false;
            if(IineModelService.use().getIineById(post.getId(),user.getId()) != null){
                //いいねがすでに押されている場合はtrueにする
                iine = true;
            }
            List<Iine> iineList = IineModelService.use().getIineListByPostId(postId);
            return ok(introduction.render(loginId,post,commentForm,comment,iine,iineList));
        }
    }

    //ユーザー情報編集のフォーム画面に遷移する
    @Security.Authenticated(Secured.class)
    public static Result updateUserForm(Long formatedUserId){
    	String loginId = session().get("loginId");
    	Long userId = formatedUserId-932108L;
    	User user = UserModelService.use().getUserById(userId);
    	// ユーザーフォームの作成
    	UserForm userFormtemp = new UserForm();
    	String profile = PostModelService.use().reverseSanitize(user.getProfile());
    	userFormtemp.setUserForm(user.getUserName(),		// ユーザー名
    							   user.getPassword(),		// パスワード
    							   user.getLoginId(),		// ログインID
    							   user.getAdmin(),			// 管理者かどうか
    							   profile,		// プロフィール
    							   user.getDepartment(),	// 部署名
    							   null,					// 画像名
    							   user.getImageData(),		// 前回の画像データ
    							   user.getImageName(),		// 前回の画像名
    							   user.getImageEncData());	// エンコーディングされたデータ
    	Form<UserForm> userForm = form(UserForm.class).fill(userFormtemp);

    	System.out.println("画像データー："+user.getImageData());
    	user.setProfile(PostModelService.use().reverseSanitize(user.getProfile()));
    	//Form<User> userForm = form(User.class).fill(user);

    	return ok(update_user.render(loginId,userForm,user));
    }

    //ユーザー情報の編集を実行する
    @Security.Authenticated(Secured.class)
    public static Result DoUpdate(Long formatedUserId){
    	String loginId = session().get("loginId");
    	Long userId = formatedUserId-932108L;
    	User user = UserModelService.use().getUserById(userId);
    	Form<UserForm> userForm = new Form(UserForm.class).bindFromRequest();

    	if(!userForm.hasErrors()){
    		System.out.println("ユーザー編集バインド、エラーなし");

    		if( userForm.get().department.isEmpty() ){
    			// 部署が空白
    			userForm.get().department = "未設定";
    		}
    		if( userForm.get().profile.isEmpty() ){
    			userForm.get().profile = "よろしくお願いします。";
    		}

    		User newUser = new User();
	    	newUser.setUserName(userForm.get().userName);
	    	newUser.setPassword(userForm.get().password);
	    	newUser.setLoginId(userForm.get().loginId);
	    	String profile = PostModelService.use().sanitizeString(userForm.get().profile);
	    	newUser.setProfile(profile);
	    	newUser.setDepartment(userForm.get().department);
	    	newUser.setAdmin(userForm.get().admin);

	    	// 画像の保存
	    	String imageName = newUser.getImageName();	// 画像の名前の保存
	    	MultipartFormData body = request().body().asMultipartFormData();
	    	FilePart image = body.getFile("imageName");

	    	// 画像を指定したかどうか
	    	String extensionName = "";		// 拡張子
	    	if( image != null ){
	    		// 新しく画像を指定された場合

	    		// 拡張子の取得
	    		newUser.setImageName(image.getFilename());
	    		int lastDotPosition = image.getFilename().lastIndexOf(".");
	    		extensionName = image.getFilename().substring(lastDotPosition + 1);

	    		// 画像->byte型に変換
	    		BufferedImage read;
	    		try{
		    		read = ImageIO.read(image.getFile());
					newUser.setImageData(new MakeImage().getBytesFromImage(read,extensionName));
					System.out.println(newUser.getImageData());
                    newUser.setImageEncData(Base64.getEncoder().encodeToString(newUser.getImageData()));
	    		}catch(Exception e){

	    		}
	    	}else if( userForm.get().imageNameOld != null ){
	    		// 画像指定されず前回の画像がある場合
	    		newUser.setImageName(userForm.get().imageNameOld);
	    		String encodingData = userForm.get().encoding;
	    		System.out.println("************"+encodingData);
                newUser.setImageEncData(encodingData);
	    		newUser.setImageData(Base64.getDecoder().decode(encodingData));
	    		System.out.println(userForm.get().imageDataOld);
	    	} else{
	    		// 画像が選択せれず、前回のデーターもなかった場合
	    	}


	    	User editedUser = UserModelService.use().updateUser(user, newUser);
	    	return redirect(controllers.routes.Application.userPage(932108L+editedUser.getId()));
    	}else{
    		System.out.println("ユーザー編集バインド、エラーあり！！！");

    		return ok(update_user.render(loginId,userForm,user));
    	}
    }

    public static Result searchPostBykeyword(Integer page,String searchedKeyword){
    	Form<SearchPostForm> searchForm = Form.form(SearchPostForm.class).bindFromRequest();
    	if(!searchForm.hasErrors()){
    		System.out.println("投稿検索バインドエラーなし");
    		String keyword = "";
            String loginId = session().get("loginId");
	    	if(searchedKeyword != ""){
	    		searchForm.get().keyword = searchedKeyword;
	    		keyword = searchedKeyword;
	    	}else{

	    		keyword = searchForm.get().keyword;
	    		System.out.println("+++++++++++++++++++++++++++++++++"+keyword);
	    		System.out.println("+++++++++++++++++++++++++++++++++");
	    	}
    		List<Post> postList = PostModelService.use().searchPostByKeyword(keyword,page);
            // いいねが押されているかの判定
            List<Boolean> booleanList = IineModelService.use().getBooleanListByPostList(postList,loginId);
    		return ok(index.render(loginId,postList,booleanList,GoodsModelService.use().getGoodsAllCategory(),page,PostModelService.use().getMaxPageForSearch(keyword),"ALL",searchForm));
    	}else{
    		System.out.println("投稿検索バインドエラーあり!!!");
    		return redirect(controllers.routes.Application.index(1,"ALL"));
    	}
    }

    // 投稿のソート
    public static Result sortPost(Integer page ,String sortName){
   		List<Post> postList = null;
    	List<String> categoryList = GoodsModelService.use().getGoodsAllCategory();				// カテゴリーリスト
    	switch(sortName){
    	case "日付新しい順":
    		postList = PostModelService.use().getPostList(page);
    		break;
    	case "日付古い順":
    		postList = PostModelService.use().getPostList(page);
    		Collections.reverse(postList);
    		break;
    	case "いいね":
    		postList = PostModelService.use().getPostIineSort(page);
    		break;
    	case "コメント":
    		postList = PostModelService.use().getPostCommentSort(page);					// 投稿リスト
    		break;
    	}
        String loginId = session().get("loginId");
        // いいねが押されているかの判定
        List<Boolean> booleanList = IineModelService.use().getBooleanListByPostList(postList,loginId);
    	return ok(index.render(loginId,postList,booleanList,categoryList,page,PostModelService.use().getMaxPage("ALL"),"ALL",Form.form(models.form.SearchPostForm.class)));
    }

    // 投稿情報の編集
    public static Result editPost(Long postId){
    	// 投稿情報取得
    	Post post = PostModelService.use().getPostListById(postId);

    	// フォームの初期化
    	UpdatePostForm postFormtemp = new UpdatePostForm();
    	postFormtemp.postTitle = post.getPostTitle();
    	postFormtemp.postComment = PostModelService.use().reverseSanitize(post.getPostComment());
    	Form<UpdatePostForm> updatePostForm = form(UpdatePostForm.class).fill(postFormtemp);

    	// ログインID取得
    	String loginId = session().get("loginId");

    	// 編集画面へ遷移
    	return ok(updatePost.render(loginId,updatePostForm,postId));
    }

    // 投稿情報の更新
    public static Result updatePost(Long postId){
    	// 投稿情報取得
    	Post post = PostModelService.use().getPostListById(postId);
    	Form<UpdatePostForm> updatePostForm = Form.form(UpdatePostForm.class).bindFromRequest();
    	if( !updatePostForm.hasErrors() ){

    		post.setPostTitle(updatePostForm.get().postTitle);
    		post.setPostComment(PostModelService.use().sanitizeString(updatePostForm.get().postComment));
    		post.update();
    		return redirect(controllers.routes.Application.introduction(postId));
    	}else{
    		// ログインID取得
    		String loginId = session().get("loginId");
    		return ok(updatePost.render(loginId,updatePostForm,postId));
    	}

    }
}
