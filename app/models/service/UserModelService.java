/*
 *	ユーザーサービスクラス
 *	@author Kotaro Nishida
 */
package models.service;

import java.util.List;

import models.entity.Post;
import models.entity.User;
import play.db.ebean.Model.Finder;

public class UserModelService {
	/*
	 *	ユーザーサービスの生成
	 *	@param なし
	 *	@return UserModelServiceのインスタンス
	 *	@author Kotaro Nishida
	 */
	public static UserModelService use(){
		return new UserModelService();
	}

	/*
	 *	保存
	 *	@param 保存したいUserオブジェクト
	 *	@return 保存したUserオブジェクト
	 *	@author Kotaro Nishida
	 */
	public User save(User user){
		if(checkUser(user) == null){
			return null;
		}
		user.save();
		return user;
	}

	/*
	 *	IDでユーザー情報を取得
	 *	@param Long userId：ユーザーのID
	 *	@return Userのオブジェクト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public User getUserById(Long userId){
		Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);
		User user = find.byId(userId);
		return checkUser(user);
	}

	/*
	 *	ユーザーIDで投稿情報を取得
	 *	@param Long userId：
	 *	@return 投稿リスト
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostByUserId(Long userId){
		Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);
		List<User> userList = find.where().eq("id", userId).findList();
		List<Post> postList = userList.get(0).getPost();
		return postList;
	}

	/*
	 *	引数のリストがnullまたは空かどうかのチェック
	 *	@param Userのリスト
	 *	@return 成功時：Userのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<User> checkUser(List<User> userList){
		if(userList == null){
			// 中身がnull
			return null;
		}
		if(userList.isEmpty()){
			// 中身が空
			return null;
		}
		return userList;
	}

	/*
	 *	引数のオブジェクトがnullまたは空かどうかのチェック
	 *	@param Userのリスト
	 *	@return 成功時：Userのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public User checkUser(User user){
		if(user == null){
			// 中身がnull
			return null;
		}
		return user;
	}

}