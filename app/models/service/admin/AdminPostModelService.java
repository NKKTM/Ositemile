/*
 *	管理者投稿モデルサービス
 *	@author Kotaro Nishida
 */
package models.service.admin;

import java.util.List;

import models.entity.Post;
import play.db.ebean.Model.Finder;

public class AdminPostModelService {
	/*
	 *	モデルサービスのインスタンス生成
	 *	@param なし
	 *	@return Postのリスト
	 *	@author Kotaro Nishdia
	 */
	public static AdminPostModelService use(){
		return new AdminPostModelService();
	}

	/*
	 *	検索した結果をリストにして返す
	 *	@param  String postTitle : 投稿タイトル
	 *			String postComment : 投稿コメント
	 *			boolean ascend: true<昇順> or false<降順>
	 *	@return Postのリスト
	 *	@author Kotaro Nishdia
	 */
	public List<Post> search(String postTitle , String postComment,boolean ascend){
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);

		// データーがあるかどうか
		if( !find.all().isEmpty() ){
			List<Post> postList = null;
			String orderby = (ascend) ? "date asce": "date desc";

			// 投稿タイトル＆投稿コメント
			if( (postTitle != null && !postTitle.isEmpty()) && (postComment != null && !postComment.isEmpty()) ){
				postList = find.where("postTitle LIKE '%"+postTitle+"%'"+" OR "+ " postComment LIKE '%"+postComment+"%'").
							  findList();
			}
			else if( postTitle != null && !postTitle.isEmpty()){
				// 投稿タイトル
				postList = find.where("postTitle LIKE '%"+postTitle+"%'").findList();
			}else if( postComment != null && !postComment.isEmpty() ){
				// 投稿コメント
				postList = find.where("postComment LIKE '%"+postComment+"%'")
									.findList();
			}else{
				// 全データ
				return find.orderBy(orderby).findList();
			}
			return postList;
		}
		return null;
	}
}
