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
import models.service.admin.AdminCommentModelService;
import models.service.admin.AdminGoodsModelService;
import models.service.admin.AdminPostModelService;
import models.service.admin.AdminUserModelService;

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
		Form<AdminCommentForm> commnetSearchForm = form(AdminCommentForm.class).bindFromRequest();
		String[] params = { "orderby" };
		DynamicForm input = Form.form();
		input = input.bindFromRequest(params);
		String on = input.data().get("orderby");
		boolean bool = Boolean.valueOf(on);
		List<Comment> comment =  AdminCommentModelService.use().search(commnetSearchForm.get().comment, commnetSearchForm.get().userName,bool);
		return ok(commentList.render("",comment,commnetSearchForm,bool));
	}

	// 削除
	@Security.Authenticated(models.login.SecuredAdmin.class)
	public static Result commentDelete(Long id){
		Form<AdminCommentForm> commnetSearchForm = form(AdminCommentForm.class).bindFromRequest();
		String[] params = { "orderby" };
		DynamicForm input = Form.form();
		input = input.bindFromRequest(params);
		String on = input.data().get("orderby");
		boolean bool = Boolean.valueOf(on);
		List<Comment> comment =  AdminCommentModelService.use().delete(id);
		return ok(commentList.render("",comment,commnetSearchForm,bool));
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
		Form<AdminUserForm> userSearchForm = form(AdminUserForm.class).bindFromRequest();
		String[] params = { "orderby" };
		DynamicForm input = Form.form();
		input = input.bindFromRequest(params);
		String on = input.data().get("orderby");
		boolean bool = Boolean.valueOf(on);
		List<User> user =  AdminUserModelService.use().search(userSearchForm.get().userName, userSearchForm.get().loginId,bool);
		return ok(userList.render("",user,userSearchForm,bool));
	}

	@Security.Authenticated(models.login.SecuredAdmin.class)
	public static Result userDelete(Long userId){
		Form<AdminUserForm> userSearchForm = form(AdminUserForm.class).bindFromRequest();
		String[] params = { "orderby" };
		DynamicForm input = Form.form();
		input = input.bindFromRequest(params);
		String on = input.data().get("orderby");
		boolean bool = Boolean.valueOf(on);

		// コメント削除
		List<Comment> comment =  CommentModelService.use().getCommetnListByPostId(userId);
		if( comment != null ){
			for(int i = 0; i < comment.size(); i++){
				AdminCommentModelService.use().delete(comment.get(i).getId());
			}
		}


		// 投稿削除
		List<Post> post = PostModelService.use().getPostListByUserId(userId);
		if( post != null ){
			for(int i = 0; i < post.size(); i++){
				AdminPostModelService.use().delete(post.get(i).getId());
			}
		}


		// ユーザー削除
		List<User> user =  AdminUserModelService.use().delete(userId);
		return ok(userList.render("",user,userSearchForm,bool));
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
		Form<AdminGoodsForm> goodsSearchForm = form(AdminGoodsForm.class).bindFromRequest();
		String[] params = { "orderby" };
		DynamicForm input = Form.form();
		input = input.bindFromRequest(params);
		String on = input.data().get("orderby");
		boolean bool = Boolean.valueOf(on);
		List<Goods> user =  AdminGoodsModelService.use().search(goodsSearchForm.get().goodsName, goodsSearchForm.get().category,bool);
		return ok(goodsList.render("",user,goodsSearchForm,bool));
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
		Form<AdminPostForm> postSearchForm = form(AdminPostForm.class).bindFromRequest();
		String[] params = { "orderby" };
		DynamicForm input = Form.form();
		input = input.bindFromRequest(params);
		String on = input.data().get("orderby");
		boolean bool = Boolean.valueOf(on);
		List<Post> post =  AdminPostModelService.use().search(postSearchForm.get().postTitle, postSearchForm.get().postComment,bool);
		return ok(postList.render("",post,postSearchForm,bool));
	}

}
