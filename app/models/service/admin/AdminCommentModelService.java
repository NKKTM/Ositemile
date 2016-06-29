/*
 *	管理者コメントモデルサービス
 *	@author Kotaro Nishida
 */
package models.service.admin;

import java.util.List;

import models.entity.Comment;
import play.db.ebean.Model.Finder;

public class AdminCommentModelService {

	/*
	 *	生成
	 *	@param なし
	 *	@return インスタンス
	 *	@author Kotaro Nishida
	 */
	public static AdminCommentModelService use(){
		return new AdminCommentModelService();
	}

	/*
	 *	検索した結果をリストにして返す
	 *	@param  String comment : コメント内容
	 *			String userName: ユーザー名
	 *			boolean ascend: true<昇順> or false<降順>
	 *	@return Commentのリスト
	 *	@author Kotaro Nishida
	 */
	public List<Comment> search(String comment,String userName,boolean ascend){
		Finder<Long, Comment> find = new Finder<Long, Comment>(Long.class, Comment.class);

		// データーがあるかどうか
		if( !find.all().isEmpty() ){
			List<Comment> commentList = null;
			String orderby = (ascend) ? "date asce": "date desc";

			// コメント
			if( (comment != null && !comment.isEmpty()) && (userName != null && !userName.isEmpty()) ){
				commentList = find.where("comment LIKE '%"+comment+"%'"+" AND "+ " human.userName LIKE '%"+userName+"%'")
						.orderBy(orderby)
						. findList();
			}
			else if( comment != null && !comment.isEmpty()){
				// コメント
				commentList = find.where("comment LIKE '%"+comment+"%'").orderBy(orderby)
						.orderBy(orderby)
						. findList();
			}else if( userName != null && !userName.isEmpty() ){
				// メッセージ
				commentList = find.where("human.userName LIKE '%"+userName+"%'")
									.orderBy(orderby).findList();
			}else{
				// 全データ
				return find.orderBy(orderby).findList();
			}return commentList;
		}
		return null;
	}

	/*
	 *	削除
	 *	@param  Long commentId : コメントのID
	 *	@return Commentのリスト
	 *	@author Kotaro Nishida
	 */
	public List<Comment> delete(Long commentId){
		Finder<Long, Comment> find = new Finder<Long, Comment>(Long.class, Comment.class);
		if( !find.all().isEmpty() ){
			// 中身が空
			Comment comment = find.ref(commentId);
			comment.delete();
			return find.all();
		}
		return null;
	}

}
