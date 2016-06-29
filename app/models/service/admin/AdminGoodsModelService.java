/*
 *	管理者商品モデルサービス
 *	@author Kotaro Nishida
 */
package models.service.admin;

import java.util.List;
import com.avaje.ebean.Query;
import models.entity.Goods;
import play.db.ebean.Model.Finder;

public class AdminGoodsModelService {
	/*
	 *	モデルサービスのインスタンス生成
	 *	@param なし
	 *	@return インスタンス
	 *	@author Kotaro Nishida
	 */
	public static AdminGoodsModelService use(){
		return new AdminGoodsModelService();
	}

	/*
	 *	検索した結果をリストにして返す
	 *	@param  String goodsName : 商品名
	 *			String category : カテゴリー
	 *			boolean ascend: true<昇順> or false<降順>
	 *	@return Goodsのリスト
	 *	@author Kotaro Nishida
	 */
	public List<Goods> search(String goodsName,String category,boolean ascend){
		Finder<Long, Goods> find = new Finder<Long, Goods>(Long.class, Goods.class);

		// データーがあるかどうか
		if( !find.all().isEmpty() ){
			List<Goods> goodsList = null;
			String orderby = (ascend) ? "id asce": "id desc";

			// 商品名&カテゴリー
			Query<Goods> goodsQuery = null;
			if( (goodsName != null && !goodsName.isEmpty()) && (category != null && !category.isEmpty()) ){
				goodsQuery = find.where("goodsName LIKE '%"+goodsName+"%'"+" OR "+ " category LIKE '%"+category+"%'");
			}
			else if( goodsName != null && !goodsName.isEmpty()){
				// 商品名
				goodsQuery = find.where("goodsName LIKE '%"+goodsName+"%'");

			}else if( category != null && !category.isEmpty() ){
				// カテゴリー
				goodsQuery = find.where("category LIKE '%"+category+"%'");

			}else{
				// 全データ
				return find.orderBy(orderby).findList();
			}return goodsQuery.orderBy(orderby).findList();
		}
		return null;
	}

	/*
	 *	削除
	 *	@param  Long goodsId : コメントのID
	 *	@return Goodsのリスト
	 *	@author Kotaro Nishida
	 */
	public List<Goods> delete(Long goodsId){
		Finder<Long, Goods> find = new Finder<Long, Goods>(Long.class, Goods.class);
		if( !find.all().isEmpty() ){
			Goods goods = find.ref(goodsId);
			goods.delete();
			return find.all();
		}
		return null;
	}
}
