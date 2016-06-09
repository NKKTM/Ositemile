/*
 *	投稿サービスクラス
 *	@author Kotaro Nishida
 */
package models.service;

import java.util.List;


import models.entity.Post;
import play.db.ebean.Model.Finder;

public class PostModelService {

	final static public Integer LIMIT = 9;  // １ページあたりの表示件数

	/*
	 *	投稿サービスの生成
	 *	@param なし
	 *	@return 投稿サービスのインスタンス
	 *	@author Kotaro Nishida
	 */
	public static PostModelService use(){
		return new PostModelService();
	}

	/*
	 *　保存
	 *	@param 保存したPostオブジェクト
	 *	@return 保存したPostオブジェクト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public Post save(Post post){
		if( post != null ){
			// postが存在している
			post.save();
			return post;
		}else{
			// postが存在していない
			return null;
		}
	}

	/*
	 *	投稿情報全件取得
	 *	@param なし
	 *	@return Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostList(){
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.orderBy("date desc").findList();
		return checkPost(postList);
	}

	/*
	 *	ページングのためのMaxページ取得
	 *	@param なし
	 *	@return MaxPage
	 *			失敗時：null
	 *	@author Hatsune Kitajima
	 */
	public int getMaxPage(){
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.orderBy("date desc").findList();
		int maxPage = postList.size()/LIMIT + 1;
		System.out.println("maxPage："+maxPage);
		return maxPage;
	}	

	/*
	 *	１ページあたりの投稿リストを取得
	 *	@param 取得したいページ数
	 *	@return Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostList(Integer pageNumber){
		Integer pageNum = (pageNumber - 1 < 0)? 0 : pageNumber - 1;
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.orderBy("date desc").findPagingList(LIMIT)
							.getPage(pageNum)
							.getList();
		return checkPost(postList);
	}

	/*
	 *	投稿IDで特定の投稿情報を取得
	 *	@param 投稿ID
	 *	@return 投稿情報
	 *			失敗時；null
	 *	@author Kotaro Nishida
	 */
	public Post getPostListById( Long postId ){
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		Post post = find.where().eq("id", postId).findList().get(0);
		return checkPost(post);
	}

	/*
	 *　タイトルで投稿リストを取得
	 *	@param String title : 投稿タイトル
	 *	@return Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostListByTitle(String title){
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.where().ilike("postTitle", '%'+title+'%').findList();
		return checkPost(postList);
	}

	/*
	 *	投稿コメントで投稿リストを取得
	 *	@param String comment : 投稿コメント
	 *	@return Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostListByComment(String comment){
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.where().ilike("postComment", '%'+comment+'%').findList();
		return checkPost(postList);
	}

	/*
	 *	商品名で投稿リストを取得
	 *	@param String goodsName: 商品名
	 *	@return Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostListByName(String goodsName){
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.where().ilike("goods.getGoodsName()", '%'+goodsName+'%').findList();
		return checkPost(postList);
	}

	/*
	 *	カテゴリで投稿リスト取得
	 *	@param String category: カテゴリー
	 *	@return Postのリスト
	 *			失敗時；null
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostListByCategory( String categoryName ){
		Finder<Long,Post> find = new Finder<Long ,Post>(Long.class,Post.class);
		List<Post> postListSize = find.all();		// データーベースに入っているリストサイズ用
		List<Post> postList = null;

		if( checkPost(postListSize) == null ){
			// データーベースにデーターがない
			return null;
		}
		// カテゴリーの中身を調べる
		for( int i = 0; i < postListSize.size(); i++ ){
			postList = find.where().ilike("goods.getCategory(i)", categoryName).findList();
		}
		return checkPost(postList);
	}

	/*
	 *	ユーザーのIdでリストを取得
	 *	@param Long userId : ユーザーのID
	 *	@return Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostListByUserId(Long userId){
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.where().eq("user.getId()", userId).findList();
		return checkPost(postList);
	}

	/*
	 *	引数のリストがnullまたは空かどうかのチェック
	 *	@param Postのリスト
	 *	@return 成功時：Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Post> checkPost(List<Post> postList){
		if(postList == null){
			// 中身がnull
			return null;
		}
		if(postList.isEmpty()){
			// 中身が空
			return null;
		}
		return postList;
	}

	/*
	 *	引数のオブジェクトがnullまたは空かどうかのチェック
	 *	@param Postのリスト
	 *	@return 成功時：Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public Post checkPost(Post post){
		if(post == null){
			// 中身がnull
			return null;
		}
		return post;
	}
}
