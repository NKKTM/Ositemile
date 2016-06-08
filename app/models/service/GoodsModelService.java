/*
 *	商品サービスクラス
 *	@author Kotaro Nishida
 */
package models.service;

import java.util.List;

import models.entity.Goods;
import play.db.ebean.Model.Finder;

public class GoodsModelService {

	/*
	 *	商品サービスの生成
	 *	@param なし
	 *	@return 商品サービスのインスタンス
	 *	@author Kotaro Nishida
	 */
	public static GoodsModelService use(){
		return new GoodsModelService();
	}

	/*
	 *	保存
	 *	@param 保存したいGoodsオブジェクト
	 *	@return 本ぞしたGoodsオブジェクト
	 *	@author Kotaro Nishida
	 */
	public Goods save(Goods goods){

		if(checkGoods(goods) == null){
			return null;
		}
		goods.save();
		return goods;
	}

	/*
	 *	IDでGoodsオブジェクト取得
	 *	@param Long goodsId：商品ID
	 *	@return Goodsオブジェクト
	 *	@author Kotaro Nishida
	 */
	public Goods getGoodsById(Long goodsId){
		Finder<Long, Goods> find = new Finder<Long, Goods>(Long.class, Goods.class);
		Goods goods = find.byId(goodsId);
		return goods;
	}

	/*
	 *	引数のリストがnullまたは空かどうかのチェック
	 *	@param Goodsのリスト
	 *	@return 成功時：Goodsのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Goods> checkGoods(List<Goods> goodsList){
		if(goodsList == null){
			// 中身がnull
			return null;
		}
		if(goodsList.isEmpty()){
			// 中身が空
			return null;
		}
		return goodsList;
	}

	/*
	 *	引数のオブジェクトがnullまたは空かどうかのチェック
	 *	@param Goodsのリスト
	 *	@return 成功時：Goodsのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public Goods checkGoods(Goods goods){
		if(goods == null){
			// 中身がnull
			return null;
		}
		return goods;
	}
}
