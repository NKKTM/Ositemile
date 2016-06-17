/*
 *	管理者いいねモデルサービス
 *	@author Hatsune Kitajima
 */
package models.service.admin;

import java.util.List;

import models.entity.Iine;
import play.db.ebean.Model.Finder;

public class AdminIineModelService {

	/*
	 *	生成
	 *	@param なし
	 *	@return インスタンス
	 *	@author Kotaro Nishida
	 */
	public static AdminIineModelService use(){
		return new AdminIineModelService();
	}


	/*
	 *	削除
	 *	@param  Long iineId : いいねのID
	 *	@return Iinetのリスト
	 *	@author Hatsune Kitajima
	 */
	public List<Iine> delete(Long iineId){
		Finder<Long, Iine> find = new Finder<Long, Iine>(Long.class, Iine.class);
		if( !find.all().isEmpty() ){
			Iine iine = find.ref(iineId);
			iine.delete();
			return find.all();
		}
		return null;
	}

}
