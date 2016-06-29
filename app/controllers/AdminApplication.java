package controllers;


import play.mvc.*;

import views.html.admin.*;

import java.util.*;
import play.data.Form;
import play.db.ebean.Model.Finder;

import static play.data.Form.*;

import play.data.DynamicForm;

import models.*;
import models.entity.*;
import models.service.*;
import models.service.admin.AdminCommentModelService;
import models.service.admin.AdminGoodsModelService;
import models.service.admin.AdminPostModelService;
import models.service.admin.AdminUserModelService;
import models.service.admin.AdminIineModelService;

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
		String commentStr =Util.replaceString(commnetSearchForm.get().comment);
		String userName = Util.replaceString(commnetSearchForm.get().userName);
		List<Comment> comment =  AdminCommentModelService.use().search(commentStr, userName, bool);
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
		Comment cmt = CommentModelService.use().getCommnetById(id);
		Post post = cmt.getPost();
		post.setCommentCnt(post.getCommentCnt()-1);
		post.update();
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
		List<User> user =  AdminUserModelService.use().search(Util.replaceString(userSearchForm.get().userName), Util.replaceString(userSearchForm.get().loginId),bool);
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
		List<Comment> comment =  CommentModelService.use().getCommetnListByUserId(userId);
		if( comment != null ){
			for(int i = 0; i < comment.size(); i++){
				Comment cmt =comment.get(i);
				Post post = cmt.getPost();
				post.setCommentCnt(post.getCommentCnt()-1);
				post.update();
				AdminCommentModelService.use().delete(comment.get(i).getId());
			}
		}

		// 投稿削除
		List<Post> post = PostModelService.use().getPostListByUserId(userId);
		if( post != null ){
			for(int i = 0; i < post.size(); i++){
				// AdminPostModelService.use().delete(post.get(i).getId());
				//ここでGoodsを消す
				// 商品削除
				Goods item = GoodsModelService.use().getGoodsByPostId(post.get(i).getId());
				if( item != null ){
					item.delete();
				}

			}
		}

		// いいね削除
		List<Iine> iine = IineModelService.use().getIineListByUserId(userId);
		if( iine != null ){
			for(int i = 0; i < iine.size(); i++){
				Iine iine1 = iine.get(i);
				Post postWithIine = iine1.getPost();
				postWithIine.setIineCnt(postWithIine.getIineCnt()-1);
				postWithIine.update();
				AdminIineModelService.use().delete(iine.get(i).getId());
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
		List<Goods> goods =  AdminGoodsModelService.use().search(Util.replaceString(goodsSearchForm.get().goodsName), Util.replaceString(goodsSearchForm.get().category),bool);
		return ok(goodsList.render("",goods,goodsSearchForm,bool));
	}

	// 削除
	@Security.Authenticated(models.login.SecuredAdmin.class)
	public static Result goodsDelete( Long postId ){
		Form<AdminGoodsForm> goodsSearchForm = form(AdminGoodsForm.class).bindFromRequest();
		String[] params = { "orderby" };
		DynamicForm input = Form.form();
		input = input.bindFromRequest(params);
		String on = input.data().get("orderby");
		boolean bool = Boolean.valueOf(on);

		// 商品削除
		Goods item = GoodsModelService.use().getGoodsByPostId(postId);
		if( item != null ){
			Finder<Long, Goods> find = new Finder<Long, Goods>(Long.class, Goods.class);
			item.delete();
		}

		// コメント削除
		List<Comment> comment =  CommentModelService.use().getCommentList(postId);
		if( comment != null ){
			for(int i = 0; i < comment.size(); i++){
				AdminCommentModelService.use().delete(comment.get(i).getId());
			}
		}

		// いいね削除
		List<Iine> iine = IineModelService.use().getIineListByPostId(postId);
		if( iine != null ){
			for(int i = 0; i < iine.size(); i++){
				AdminIineModelService.use().delete(iine.get(i).getId());
			}
		}


		List<Goods> goods = GoodsModelService.use().getGoodsList();
		return ok(goodsList.render("",goods,goodsSearchForm,bool));
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
		List<Post> post =  AdminPostModelService.use().search(Util.replaceString(postSearchForm.get().postTitle), Util.replaceString(postSearchForm.get().postComment),bool);
		return ok(postList.render("",post,postSearchForm,bool));
	}

	// 削除
	@Security.Authenticated(models.login.SecuredAdmin.class)
	public static Result postDelete(Long postId){
		Form<AdminPostForm> postSearchForm = form(AdminPostForm.class).bindFromRequest();
		String[] params = { "orderby" };
		DynamicForm input = Form.form();
		input = input.bindFromRequest(params);
		String on = input.data().get("orderby");
		boolean bool = Boolean.valueOf(on);

		// 商品削除
		Goods goods = GoodsModelService.use().getGoodsByPostId(postId);
		if( goods != null ){
			Finder<Long, Goods> find = new Finder<Long, Goods>(Long.class, Goods.class);

			goods.delete();
		}

		// コメント削除
		List<Comment> comment =  CommentModelService.use().getCommentList(postId);
		if( comment != null ){
			for(int i = 0; i < comment.size(); i++){
				AdminCommentModelService.use().delete(comment.get(i).getId());
			}
		}

		// いいね削除
		List<Iine> iine = IineModelService.use().getIineListByPostId(postId);
		if( iine != null ){
			for(int i = 0; i < iine.size(); i++){
				AdminIineModelService.use().delete(iine.get(i).getId());
			}
		}

		// 投稿取得
		List<Post> post =  PostModelService.use().getPostList();
		return ok(postList.render("",post,postSearchForm,bool));
	}

}
