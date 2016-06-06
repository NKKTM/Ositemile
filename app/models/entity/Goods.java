/*
 *	商品クラス
 *	@author Kotaro Nishida
 */
package models.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Goods {

	@Id
	private Long			_id;				// ID
	private String			_goodsName;			// 商品名
	private String			_imageUrl;			// 画像URL
	private String			_amazonUrl;			// AmazonのURL
	private List<String>	_category;			// カテゴリリスト名

	@OneToMany(mappedBy="_goods",cascade = CascadeType.ALL)
	private Post			_post;				// 投稿情報

	//*****セッター・ゲッター*****

	// ID
	public void setId(Long id){
		this._id = id;
	}
	public Long getId(){
		return _id;
	}

	// 商品名
	public void setGoodsName(String name){
		this._goodsName = name;
	}
	public String getGoodsName(){
		return _goodsName;
	}

	// 画像URL
	public void setImageUrl(String imageUrl){
		this._imageUrl = imageUrl;
	}
	public String getImageUrl(){
		return _imageUrl;
	}

	// AmazonURL
	public void setAmazonUrl(String amazonUrl){
		this._amazonUrl = amazonUrl;
	}
	public String getAmazonUrl(){
		return _amazonUrl;
	}

	// カテゴリーリスト
	public void setCategory(List<String> category){
		this._category = category;
	}
	public List<String> getCategory(){
		return _category;
	}
}
