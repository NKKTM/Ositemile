/*
 *	いいねサービスクラス
 *	@author Hatsune Kitajima
 */
package models.service;

import java.util.List;

import models.entity.Iine;
import play.db.ebean.Model.Finder;

public class IineModelService {

	/*
	 *	いいねサービスの生成
	 *	@param なし
	 *	@return いいねサービスのインスタンス
	 *	@author Hatsune Kitajima
	 */
	public static IineModelService use(){
		return new IineModelService();
	}

	/*
	 *	投稿IDと一致したいいねリスト
	 *	@param なし
	 *	@return いいねのリスト
	 *	@author Hatsune Kitajima
	 */
	public List<Iine> getIineListByPostId(Long postId){
		Finder<Long, Iine> find = new Finder<Long, Iine>(Long.class, Iine.class);
		return find.where().eq("post.id",postId).findList();
	}

	/*
	 *	ユーザーIDと一致したリスト取得
	 *	@param Long userId : ユーザーID
	 *	@return いいねのリスト
	 *	@author Hatsune Kitajima
	 */
	public List<Iine> getIineListByUserId(Long userId){
		Finder<Long, Iine> find = new Finder<Long, Iine>(Long.class, Iine.class);
		List<Iine> iineList =  find.where().eq("user.id",userId).findList();
		return iineList;
	}

	/*
	 *	ユーザーIDとPostID両方に一致したいいね取得
	 *	@param Long userId : ユーザーID, Long postId ：ポストID
	 *	@return いいね
	 *	@author Hatsune Kitajima
	 */
	public Iine getIineById(Long postId, Long userId){
		Finder<Long, Iine> find = new Finder<Long, Iine>(Long.class, Iine.class);
		Iine iine =  find.where("post.id = "+postId+ " and user.id = "+userId).findUnique();;
		return iine;
	}	

	
}
