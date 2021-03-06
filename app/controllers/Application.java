package controllers;

import play.*;


import play.mvc.*;

import play.data.Form;
import com.avaje.ebean.*;

import static play.data.Form.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.List;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

import org.w3c.dom.Element;

import javax.imageio.ImageIO;


import play.libs.Json;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.data.DynamicForm;

import views.html.*;
import views.html.login.*;
import views.html.post.*;
import views.html.iine.*;


import models.entity.*;
import models.entity.Comment;
import models.form.*;
import models.login.*;
import models.service.*;
import models.MakeImage;
import models.amazon.*;

import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

import org.apache.commons.lang3.StringUtils;




public class Application extends Controller {

    // 定数
    // rakutenAPIにリクエストするURLの固定部分
    //private static final String AMAZON_URL = "https://app.rakuten.co.jp/services/api/IchibaItem/Search/20140222?applicationId=1084889951156254811&format=xml&sort=-reviewCount&keyword=";
	//ソート:standard
	private static final String AMAZON_URL = "https://app.rakuten.co.jp/services/api/IchibaItem/Search/20140222?applicationId=1084889951156254811&format=xml&sort=standard&keyword=";

    //楽天ジャンル検索APIにリクエストするURLの固定部分
    private static final String RAKUTEN_GENRE_URL = "https://app.rakuten.co.jp/services/api/IchibaGenre/Search/20140222?applicationId=1084889951156254811&format=xml&genreId=";


    public static Result index(Integer page,String category,String sortName) {

    	 //pageが正しい値かどうかチェック
    	if(1 <= PostModelService.use().getMaxPage(category)) {
	    	if(!models.Util.checkPaging(page, PostModelService.use().getMaxPage(category))){
	    		 return redirect(routes.Application.index(1,category,sortName));
	    	}
    	}else if( PostModelService.use().getMaxPage(category) == 0 && page != 1){
    		return redirect(routes.Application.index(1,category,sortName));
    	}

        // ポストのリスト取得
        List<Post> postList;
        int postListSize = 0;
        if(category.equals("ALL")){
            postList = PostModelService.use().getPostList(page);
            if(PostModelService.use().getPostList() != null){
                postListSize = PostModelService.use().getPostList().size();
            }
        }else{
            postList = PostModelService.use().getPostListByCategory(page,category);
            if(PostModelService.use().getPostListByCategory(category) != null){
                postListSize = PostModelService.use().getPostListByCategory(category).size();
            }
        }
        // セッションId取得
        String loginId = session().get("loginId");
        // いいねが押されているかの判定
        List<Boolean> booleanList = IineModelService.use().getBooleanListByPostList(postList,loginId);

        return ok(index.render(loginId,postList,booleanList,GoodsModelService.use().getGoodsAllCategory(),page,PostModelService.use().getMaxPage(category),category,Form.form(models.form.SearchPostForm.class),sortName,postListSize));
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
            //セッションにloginIdを登録
            session().clear();
            session("loginId", user.getLoginId());
            return redirect("/");
        }else{
        	//ユーザー情報がフォームから取得できなかった場合
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
        return redirect("/");
    }

    // 管理者ログイン
    @Security.Authenticated(SecuredAdmin.class)
    public static Result admin() {
        return redirect("/postList");
    }



    // 投稿するアイテムを検索する
    @Security.Authenticated(Secured.class)
    public static Result postSearchItem() throws Exception{
    	Form<SearchItemForm> searchForm = form(SearchItemForm.class).bindFromRequest();
        Form<Goods> goodsForm = Form.form(Goods.class);
        List<Goods> goodsList = new ArrayList<Goods>();
        if(searchForm.hasErrors()){
        	return ok(postSearchItem.render(session().get("loginId"),null,goodsForm,searchForm));
        }else{
        	if(StringUtils.isBlank(searchForm.get().searchWord)){
        		return ok(postSearchItem.render(session().get("loginId"),goodsList,goodsForm,searchForm));
        	}
	        String searchWordStr = searchForm.get().searchWord;
	        searchWordStr = URLEncoder.encode(searchWordStr,"utf-8");
	        // URLと結合
	        String searchUrl = AMAZON_URL + searchWordStr;
	        Element elementRoot = AmazonModelService.use().getElement(searchUrl);
	        if(elementRoot == null){
	        	return ok(postSearchItem.render(session().get("loginId"),goodsList,goodsForm,searchForm));
	        }
	        goodsList = AmazonModelService.use().getSearchedGoodsList(elementRoot);
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
    		Goods item = new Goods();
    		item.setGoodsName(goodsForm.get().getGoodsName());
    		item.setImageUrl(goodsForm.get().getImageUrl());
    		item.setAmazonUrl(goodsForm.get().getAmazonUrl());
    		item.setGenreId(goodsForm.get().getGenreId());

            // 検索して遷移する時にsessionを消す
            String ps = session("postSession");
            session().remove("postSession");

    		return ok(postInput.render(session().get("loginId"),goodsForm,postForm,item));
    	}else{
    		//フォームにエラーあり
    		return ok(postSearchItem.render(session().get("loginId"),null,form(Goods.class),new Form<>(SearchItemForm.class)));
    	}
    }
    //postとgoodsをdbに保存
    @Security.Authenticated(Secured.class)
    public static Result postCreate() throws Exception{
    	Form<Goods> goodsForm = form(Goods.class).bindFromRequest();
    	Form<Post> postForm = form(Post.class).bindFromRequest();
    	if(!goodsForm.hasErrors() && session("postSession") == null){

    		if(!postForm.hasErrors()){
    			//Goodsのフォームにも、postのフォームにもエラ-がない時
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

                // 登録ができたらsession追加
                session("postSession", "true");

        	}else{
        		//エラー：postのフォームにのみエラ-がある時
        		Goods item = new Goods(goodsForm.get().getGoodsName(),goodsForm.get().getImageUrl(),goodsForm.get().getAmazonUrl(),goodsForm.get().getGenreId());
        		return ok(postInput.render(session().get("loginId"),goodsForm,postForm,item));
        	}

    	}else{
    		//エラー：Goodsのフォームにエラーがある時,または連続投稿の時
    	}
    	return redirect("/");
    }

    //ユーザーページ
    public static Result userPage(Long formatedUserId){
    	 Long userId = formatedUserId-932108L;
    	 User user = UserModelService.use().getUserById(userId);

    	 List<Post> postList = PostModelService.use().getPostListByUserId(userId);
         if(postList == null){
            postList = new ArrayList<Post>();
         }
         String loginId = session().get("loginId");

    	 if(user != null){
         List<Iine> iineList = IineModelService.use().getIineListByUserId(userId);
         Collections.reverse(iineList);
         // いいねが押されているかの判定（postに対して）
         List<Boolean> postBooleanList = IineModelService.use().getBooleanListByPostList(postList,loginId);
         // いいねが押されているかの判定（iineに対して）
         List<Boolean> iineBooleanList = IineModelService.use().getBooleanListByIineList(iineList,loginId);
         return ok(user_page.render(loginId,user,postList,iineList,postBooleanList,iineBooleanList));
         } else {
             return ok(user_page.render(loginId,null,null,null,null,null));
         }
    }

    //loginIdからユーザーページのリンクを作る
    @Security.Authenticated(Secured.class)
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
        Post post = PostModelService.use().getPostById(postId);
    	if( post != null ){
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
        Post post = PostModelService.use().getPostById(postId);

        if (iineBtn != null) {
            // いいねボタンの値が取得できた時
            if(iineBtn.equals("true")){
                // いいねボタンの値がtrueのとき（いいね保存）
                result.put("iineBtn", "true");
                if(IineModelService.use().getIineById(post.getId(),user.getId()) == null){
                    //すでにこのpostIdとuserIdの組み合わせで登録されていなければセーブ
                    Iine iine = new Iine(post,user);
                    // トランザクション開始
                    Ebean.beginTransaction();
                    try {
                        iine.save();
                        Ebean.commitTransaction(); // コミット
                    }catch (Exception ex) {
                      Logger.error("ロールバックします", ex);
                      Ebean.rollbackTransaction(); // ロールバック
                    } finally {
                      Ebean.endTransaction();
                    }
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
        Post post = PostModelService.use().getPostById(postId);
        // commentform取得
        Form<CommentForm> commentForm = form(CommentForm.class).bindFromRequest();

        if( !commentForm.hasErrors() ){

            // エラーがない
            if(loginId == null){
                return redirect(controllers.routes.Application.login());
            }
          //入力値がスペースの時
            if(StringUtils.isBlank(commentForm.get().comment)){
            	return redirect(controllers.routes.Application.introduction(postId));
            }
            // コメント登録
            commentForm.get().comment = PostModelService.use().sanitizeString(commentForm.get().comment);
            Comment comment = new Comment(commentForm.get().comment,UserModelService.use().getUserByLoginId(loginId),post);
            comment.setDateStr(PostModelService.use().getDateString());
            CommentModelService.use().save(comment);

            // postにコメント情報を格納
            Post updatePost = PostModelService.use().getPostById(postId);
            updatePost.getComment().add(comment);
            updatePost.setCommentCnt(PostModelService.use().getPostById(postId).getComment().size());	// コメント数の保存
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
    @Security.Authenticated(SecuredUpdateUser.class)
    public static Result updateUserForm(Long formatedUserId){
    	String loginId = session().get("loginId");
    	Long userId = formatedUserId-932108L;
    	User user = UserModelService.use().getUserById(userId);
    	// ユーザーフォームの作成
    	UserForm userFormtemp = new UserForm();
    	if(user != null){
	    	String profile = PostModelService.use().reverseSanitize(user.getProfile());
	    	userFormtemp.setUserForm(user.getUserName(),		// ユーザー名
	    							   user.getPassword(),		// パスワード
	    							   user.getLoginId(),		// ログインID
	    							   user.getAdmin(),			// 管理者かどうか
	    							   profile,					// プロフィール
	    							   user.getDepartment(),	// 部署名
	    							   null,					// 画像名
	    							   user.getImageData(),		// 前回の画像データ
	    							   user.getImageName(),		// 前回の画像名
	    							   user.getImageEncData());	// エンコーディングされたデータ
	    	Form<UserForm> userForm = form(UserForm.class).fill(userFormtemp);

	    	user.setProfile(PostModelService.use().reverseSanitize(user.getProfile()));

	    	return ok(update_user.render(loginId,userForm,user,false));
    	}else{
    		return redirect(controllers.routes.Application.logout());
    	}
    }

    //ユーザー情報の編集を実行する
    @Security.Authenticated(Secured.class)
    public static Result DoUpdate(Long formatedUserId){
    	String loginId = session().get("loginId");
    	Long userId = formatedUserId-932108L;
    	User user = UserModelService.use().getUserById(userId);
    	Form<UserForm> userForm = new Form(UserForm.class).bindFromRequest();

    	if(!userForm.hasErrors()){

    		if( StringUtils.isBlank(userForm.get().department) ){
    			// 部署が空白
    			userForm.get().department = "未設定";
    		}
    		if( StringUtils.isBlank(userForm.get().profile) ){
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
		    		if( read == null ){
		    			return ok(update_user.render(loginId,userForm,user,true));
		    		}
					newUser.setImageData(new MakeImage().getBytesFromImage(read,extensionName));
                    newUser.setImageEncData(Base64.getEncoder().encodeToString(newUser.getImageData()));
	    		}catch(Exception e){

	    		}
	    	}else if( userForm.get().imageNameOld != null ){
	    		// 画像指定されず前回の画像がある場合
	    		newUser.setImageName(userForm.get().imageNameOld);
	    		String encodingData = userForm.get().encoding;
	    		BufferedImage read;
    			newUser.setImageData(user.getImageData());
    			newUser.setImageEncData(user.getImageEncData());
	    	} else{
	    		// 画像が選択せれず、前回のデーターもなかった場合
	    	}
	    	User editedUser = UserModelService.use().updateUser(user, newUser);
	    	return redirect(controllers.routes.Application.userPage(932108L+editedUser.getId()));
    	}else{

    		return ok(update_user.render(loginId,userForm,user,false));
    	}
    }


    //投稿のキーワード検索(タイトル、本文、ユーザー、商品名から検索)
    public static Result searchPostBykeyword(Integer page,String searchedKeyword,String sortName){
    	Form<SearchPostForm> searchForm = Form.form(SearchPostForm.class).bindFromRequest();

    	//ソートネームが不正な場合
    	if( !"日付新しい順".equals(sortName) && !"日付古い順".equals(sortName) && !"いいね".equals(sortName) && !"コメント".equals(sortName)){
    		return badRequest(views.html.errorpage.render(play.mvc.Controller.session().get("loginId")));
    	}

    	if(!searchForm.hasErrors()){
    		String keyword = "";
	    	if(searchedKeyword != ""){
                //入力されたキーワードが空ではないとき
	    		searchForm.get().keyword = searchedKeyword;
	    		keyword = searchedKeyword;
	    	}else{
                //入力されたキーワードが空のとき
                return redirect(controllers.routes.Application.index(1,"ALL","日付新しい順"));
	    	}

	    	keyword = models.Util.replaceString(keyword);


	    	//pageが正しいかチェック
	    	if(1 <= PostModelService.use().getMaxPageForSearch(keyword)) {
		        if(!models.Util.checkPaging(page, PostModelService.use().getMaxPageForSearch(keyword))){
		        	return redirect(controllers.routes.Application.searchPostBykeyword(1,searchedKeyword, sortName));
		        }
	        }else if(0 == PostModelService.use().getMaxPageForSearch(keyword) && page != 1){
	        	return redirect(controllers.routes.Application.searchPostBykeyword(1,searchedKeyword, sortName));
	        }



            // PostList取得
            List<Post> postList = new ArrayList<Post>();
            switch(sortName){
            case "日付新しい順":
                postList = PostModelService.use().searchPostByKeyword(keyword,page);
                break;
            case "日付古い順":
                postList = PostModelService.use().searchPostOldSortByKeyword(keyword,page);
                break;
            case "いいね":
                postList = PostModelService.use().searchPostIineSortByKeyword(keyword,page);
                break;
            case "コメント":
                postList = PostModelService.use().searchPostCommentSortByKeyword(keyword,page);
                break;
            }
            //indexに必要な値を取得
            String loginId = session().get("loginId");
            List<Boolean> booleanList = IineModelService.use().getBooleanListByPostList(postList,loginId);
            int postListSize = PostModelService.use().searchPostByKeyword(keyword).size();

    		return ok(index.render(loginId,postList,booleanList,GoodsModelService.use().getGoodsAllCategory(),page,PostModelService.use().getMaxPageForSearch(keyword),"ALL",searchForm,sortName,postListSize));
    	}else{
    		return redirect(controllers.routes.Application.index(1,"ALL","日付新しい順"));
    	}
    }

    // 投稿のソート
    public static Result sortPost(Integer page ,String category,String sortName){

    	//ソートネームが不正な場合
    	if( !"日付新しい順".equals(sortName) && !"日付古い順".equals(sortName) && !"いいね".equals(sortName) && !"コメント".equals(sortName)){
    		return badRequest(views.html.errorpage.render(play.mvc.Controller.session().get("loginId")));
    	}
    	//pageが正しい値かどうかチェック
    	if(1 <= PostModelService.use().getMaxPage(category)){
	        if(!models.Util.checkPaging(page, PostModelService.use().getMaxPage(category))){
	        	return redirect(controllers.routes.Application.sortPost(1 , category, sortName));
	        }
        }else if( PostModelService.use().getMaxPage(category) == 0 && page != 1){
        	return redirect(controllers.routes.Application.sortPost(1 , category, sortName));
        }

   		List<Post> postList = null;
        int postListSize = 0;
        if(category.equals("ALL")){
            //カテゴリがALLの場合
            if(PostModelService.use().getPostList() != null){
                postListSize = PostModelService.use().getPostList().size();
            }
        	switch(sortName){
        	case "日付新しい順":
        		postList = PostModelService.use().getPostList(page);
        		break;
        	case "日付古い順":
        		postList = PostModelService.use().getPostListOld(page);
        		break;
        	case "いいね":
        		postList = PostModelService.use().getPostIineSort(page);
        		break;
        	case "コメント":
        		postList = PostModelService.use().getPostCommentSort(page);
        		break;
        	}
        }else{
            //カテゴリがそれ以外の場合
        	if(PostModelService.use().getPostListByCategory(category)!=null){
	            postListSize = PostModelService.use().getPostListByCategory(category).size();
	            switch(sortName){
	            case "日付新しい順":
	                postList = PostModelService.use().getPostListByCategory(page,category);
	                break;
	            case "日付古い順":
	                postList = PostModelService.use().getPostListOld(page,category);
	                break;
	            case "いいね":
	                postList = PostModelService.use().getPostIineSort(page,category);
	                break;
	            case "コメント":
	                postList = PostModelService.use().getPostCommentSort(page,category);
	                break;
	            }
            }
        }
        // indexに必要な値を取得
        List<String> categoryList = GoodsModelService.use().getGoodsAllCategory();
        String loginId = session().get("loginId");
        List<Boolean> booleanList = IineModelService.use().getBooleanListByPostList(postList,loginId);

    	return ok(index.render(loginId,postList,booleanList,categoryList,page,PostModelService.use().getMaxPage(category),category,Form.form(models.form.SearchPostForm.class),sortName,postListSize));
    }

    // 投稿情報の編集
    @Security.Authenticated(SecuredUpdatePost.class)
    public static Result editPost(Long postId){
    	// 投稿情報取得
    	Post post = PostModelService.use().getPostById(postId);

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
    	Post post = PostModelService.use().getPostById(postId);
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

    //ランキング
    public static Result rankingPost(String type) throws ParseException{
    	List<Post> postList = new ArrayList<Post>();
    	int flag = 1;
    	switch(type){
    		case "good":
    			flag = 1;
    			postList = PostModelService.use().getRankIine();
    			break;
    		case "good-weekly":
    			flag = 2;
    			postList = PostModelService.use().getRankIineWeek();
    			break;
    		case "cmt":
    			flag = 3;
    			postList = PostModelService.use().getRankCmt();
    			break;
    		case "cmt-weekly":
    			flag = 4;
    			postList = PostModelService.use().getRankCmtWeek();
    			break;
    	}
    	return ok(ranking.render(session().get("loginId"),postList,flag));
    }

}
