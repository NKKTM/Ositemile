/*
 *	コメントサービスクラス
 *	@author Kotaro Nishida
 */
package models.service;

import java.util.List;

import models.entity.Comment;
import play.db.ebean.Model.Finder;

public class CommentModelService {

	/*
	 *	コメントサービスの生成
	 *	@param なし
	 *	@return コメントサービスのインスタンス
	 *	@author Kotaro Nishida
	 */
	public static CommentModelService use(){
		return new CommentModelService();
	}

	/*
	 *	保存
	 *	@param 保存したいCommentオブジェクト
	 *	@return 保存したCommentオブジェクト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public Comment save(Comment comment){
		if( comment != null ){
			// コメントが存在している
			comment.save();
			return comment;
		}else{
			// コメントが存在していない
			return null;
		}
	}

	/*
	 *	全コメントリスト
	 *	@param なし
	 *	@return コメントのリスト
	 *	@author Kotaro Nishida
	 */
	public List<Comment> getCommnetList(){
		Finder<Long, Comment> find = new Finder<Long, Comment>(Long.class, Comment.class);
		List<Comment> commentList = find.orderBy("date desc").findList();
		return commentList;
	}

	/*
	 *	投稿IDと一致した投稿のコメントリスト
	 *	@param なし
	 *	@return コメントのリスト
	 *	@author Kotaro Nishida
	 */
	public List<Comment> getCommentList(int postId){
		Finder<Long, Comment> find = new Finder<Long, Comment>(Long.class, Comment.class);
		return find.where().eq("post.getPost().getId()",postId).findList();
	}

	/*
	 *	引数のリストがnullまたは空かどうかのチェック
	 *	@param Commentのリスト
	 *	@return 成功時：Commentのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Comment> checkComment(List<Comment> commentList){
		if(commentList == null){
			// 中身がnull
			return null;
		}
		if(commentList.isEmpty()){
			// 中身が空
			return null;
		}
		return commentList;
	}

	/*
	 *	引数のオブジェクトがnullまたは空かどうかのチェック
	 *	@param Commentのリスト
	 *	@return 成功時：Commentのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public Comment checkComment(Comment comment){
		if(comment == null){
			// 中身がnull
			return null;
		}
		return comment;
	}
}
