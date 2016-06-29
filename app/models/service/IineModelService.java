/*
 *	いいねサービスクラス
 *	@author Hatsune Kitajima
 */
package models.service;

import java.util.List;
import java.util.ArrayList;

import models.entity.Iine;
import models.entity.User;
import models.entity.Post;
import models.service.UserModelService;

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
		return find.where().eq("post.id",postId).orderBy("date desc").findList();
	}

	/*
	 *	ユーザーIDと一致したリスト取得
	 *	@param Long userId : ユーザーID
	 *	@return そのユーザーに対して押されたいいねのリスト
	 *	@author Hatsune Kitajima
	 */
	public List<Iine> getIineListByUserId(Long userId){
		Finder<Long, Iine> find = new Finder<Long, Iine>(Long.class, Iine.class);
		List<Iine> iineList =  find.where().eq("human.id",userId).findList();
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
		Iine iine =  find.where("post.id = "+postId+ " and human.id = "+userId).findUnique();;
		return iine;
	}

	/*
	 *	PostListに対して、現在のloginIdがPostに対していいねを押しているかどうかのBooleanListを返す
	 *	@param String , List<Post> ：ポストID
	 *	@return List<Boolean>
	 *	@author Hatsune Kitajima
	 */	
	public List<Boolean> getBooleanListByPostList(List<Post> postList, String loginId){
		List<Boolean> booleanList  = new ArrayList<Boolean>();
		User user = UserModelService.use().getUserByLoginId(loginId);
		if(user != null && postList != null){
			//ログインしているかつ投稿がある場合のみ実行
			for(Post post :postList){
				if(getIineById(post.getId(),user.getId()) != null){
					//いいねが押されている場合
					booleanList.add(true);
				}else{
					//いいねが押されていない場合
					booleanList.add(false);
				}
			}			
		}
		return booleanList;
	}

	/*
	 *	iineListに対して、現在のloginIdがPostに対していいねを押しているかどうかのBooleanListを返す
	 *	@param String , List<Iine> ：ポストID
	 *	@return List<Boolean>
	 *	@author Hatsune Kitajima
	 */	
	public List<Boolean> getBooleanListByIineList(List<Iine> iineList, String loginId){
		List<Boolean> booleanList  = new ArrayList<Boolean>();
		User user = UserModelService.use().getUserByLoginId(loginId);
		if(user != null && iineList != null){
			//ログインしているかつ投稿がある場合のみ実行
			for(Iine iine :iineList){
				if(getIineById(iine.getPost().getId(),user.getId()) != null){
					//いいねが押されている場合
					booleanList.add(true);
				}else{
					//いいねが押されていない場合
					booleanList.add(false);
				}
			}			
		}
		return booleanList;
	}	 		 	

	
}
