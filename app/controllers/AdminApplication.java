package controllers;

import play.*;





import play.mvc.*;

import views.html.*;
import views.html.admin.*;

import java.text.SimpleDateFormat;
import java.util.*;
import play.data.Form;
import static play.data.Form.*;

import play.data.DynamicForm;

import models.*;
import models.entity.*;
import models.service.*;

import com.avaje.ebean.Ebean;

import models.form.admin.*;


public class AdminApplication extends Controller {

	//*******コメント********

	// commnetリスト
	@Security.Authenticated(models.login.SecuredAdmin.class)
	public static Result commentList(){
		// コメント情報取得
		List<Comment> comment = CommentModelService.use().getCommnetList();
		return ok(commentList.render("",comment,new Form(AdminCommentForm.class),true));
	}

	// commnet検索
	@Security.Authenticated(models.login.SecuredAdmin.class)
	public static Result commentSearch(){
		return TODO;
	}

	//*******ユーザー********

	// ユーザーリスト
	@Security.Authenticated(models.login.SecuredAdmin.class)
	public static Result userList(){
		// ユーザー情報を取得
		List<User> user = UserModelService.use().getUserList();
		return ok(userList.render("",user,new Form(AdminUserForm.class),true));
	}

	// ユーザー検索
	@Security.Authenticated(models.login.SecuredAdmin.class)
	public static Result userSearch(){
		return TODO;
	}

	//*******商品********

	// 商品リスト
	@Security.Authenticated(models.login.SecuredAdmin.class)
	public static Result goodsList(){
		// 商品情報を取得
		List<Goods> goods = GoodsModelService.use().getGoodsList();
		return ok(goodsList.render("",goods,new Form(AdminUserForm.class),true));
	}

	// 商品検索
	@Security.Authenticated(models.login.SecuredAdmin.class)
	public static Result goodsSearch(){
		return TODO;
	}

	//*******投稿********

	// 投稿リスト
	@Security.Authenticated(models.login.SecuredAdmin.class)
	public static Result postList(){
		// 投稿情報を取得
		List<Post> post = PostModelService.use().getPostList();
		return ok(postList.render("",post,new Form(AdminPostForm.class),true));
	}

	// 検索
	@Security.Authenticated(models.login.SecuredAdmin.class)
	public static Result postSearch(){
		return TODO;
	}

}
