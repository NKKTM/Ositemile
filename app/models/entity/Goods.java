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

import play.db.ebean.Model;

@Entity
public class Goods  extends Model{

	@Id
	private Long			id;				// ID
	private String			goodsName;			// 商品名
	private String			imageUrl;			// 画像URL
	private String			amazonUrl;			// AmazonのURL
	private List<String>	category;			// カテゴリリスト名

	@OneToMany(mappedBy="goods",cascade = CascadeType.ALL)
	private Post			post;				// 投稿情報

	//*****セッター・ゲッター*****

	// ID
	public void setId(Long id){
		this.id = id;
	}
	public Long getId(){
		return id;
	}

	// 商品名
	public void setGoodsName(String name){
		this.goodsName = name;
	}
	public String getGoodsName(){
		return goodsName;
	}

	// 画像URL
	public void setImageUrl(String imageUrl){
		this.imageUrl = imageUrl;
	}
	public String getImageUrl(){
		return imageUrl;
	}

	// AmazonURL
	public void setAmazonUrl(String amazonUrl){
		this.amazonUrl = amazonUrl;
	}
	public String getAmazonUrl(){
		return amazonUrl;
	}

	// カテゴリーリスト
	public void setCategory(List<String> category){
		this.category = category;
	}
	public List<String> getCategory(){
		return category;
	}
}
