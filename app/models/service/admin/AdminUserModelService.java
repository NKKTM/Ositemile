/*
 *	管理者ユーザモデルサービス
 *	@author Kotaro Nishida
 */
package models.service.admin;

import java.util.List;
import models.entity.User;
import play.db.ebean.Model.Finder;

public class AdminUserModelService {
	/*
	 *	モデルサービスのインスタンス生成
	 *	@param なし
	 *	@return インスタンス
	 *	@author Kotaro Nishida
	 */
	public static AdminUserModelService use(){
		return new AdminUserModelService();
	}

	/*
	 *	検索した結果をリストにして返す
	 *	@param  String : userName
	 *			String : loginId
	 *			boolean ascend: true<昇順> or false<降順>
	 *	@return Userのリスト
	 *	@author Kotaro Nishida
	 */
	public List<User> search(String userName,String loginId,boolean ascend){
		Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);

		// データーがあるかどうか
		if( !find.all().isEmpty() ){
			List<User> userList = null;
			String orderby = (ascend) ? "id asce": "id desc";

			// ユーザー名&ログインID
			if( (userName != null && !userName.isEmpty()) && (loginId != null && !loginId.isEmpty()) ){
				userList = find.where("userName LIKE '%"+userName+"%'"+" OR "+ " loginId LIKE '%"+loginId+"%'")
							.orderBy(orderby)
							. findList();
			}
			else if( userName != null && !userName.isEmpty()){
				// ユーザー名
				userList = find.where("userName LIKE '%"+userName+"%'")
							.orderBy(orderby)
							. findList();
			}else if( loginId != null && !loginId.isEmpty() ){
				// ログインID
				userList = find.where("loginId LIKE '%"+loginId+"%'")
							.orderBy(orderby)
							. findList();
			}else{
				// 全データ
				return find.orderBy(orderby).findList();
			}return userList;
		}
		return null;
	}

	/*
	 *	削除
	 *	@param Long userId : ユーザーのID
	 *	@return Userのリスト
	 *	@author Kotaro Nishida
	 */
	public List<User> delete(Long userId){
		Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);
		if( !find.all().isEmpty() ){
			User user = find.ref(userId);
			user.delete();
			return find.all();
		}
		return null;
	}
}
