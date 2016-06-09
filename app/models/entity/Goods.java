/*
 *	商品クラス
 *	@author Kotaro Nishida
 */
package models.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.db.ebean.Model;

@Entity
public class Goods  extends Model{

	@Id
	private Long			id;					// ID
	private String			goodsName;			// 商品名
	private String			imageUrl;			// 画像URL
	private String			amazonUrl;			// AmazonのURL
	private String          genreId;            //楽天のジャンルid
	private String          category;			// カテゴリリスト名

	@OneToMany(mappedBy="goods",cascade = CascadeType.ALL)
	private Post			post;				// 投稿情報

	/*
	 *	デフォルトコンストラクタ
	 *	@param なし
	 *	@return
	 *	@author Kotaro Nishida
	 */
	public Goods(){
		this.id = 0L;
		this.goodsName = "";
		this.imageUrl = "";
		this.amazonUrl = "";
		this.category = "";
	}

	/*
	 * コンストラクタ(パラメーター)
	 *	@param  String goodsName：商品名
	 *			String imageUrl;：画像URL
	 *			String amazonUrl：AmazonのURL
	 *			String category：ジャンルid
	 *	@return
	 *	@author yuki kawakami
	 */
	public Goods(String goodsName,
				 String imageUrl,
				 String amazonUrl,
				 String genreId){
		this.goodsName = goodsName;
		this.imageUrl = imageUrl;
		this.amazonUrl = amazonUrl;
		this.genreId = genreId;
	}

	/*
	 *	コンストラクタ(パラメーター)
	 *	@param  String goodsName：商品名
	 *			String imageUrl;：画像URL
	 *			String amazonUrl：AmazonのURL
	 *			List<String> category：カテゴリー
	 *			Post post：投稿情報
	 *	@return
	 *	@author Kotaro Nishida
	 */
	public Goods(String goodsName,
				 String imageUrl,
				 String amazonUrl,
				 String category,
				 Post post){
		this.goodsName = goodsName;
		this.imageUrl = imageUrl;
		this.amazonUrl = amazonUrl;
		this.category = category;
		this.post = post;
	}

	/*
	 *	パラメーターの設定
	 *	@param  String goodsName：商品名
	 *			String imageUrl;：画像URL
	 *			String amazonUrl：AmazonのURL
	 *			List<String> category：カテゴリー
	 *			Post post：投稿情報
	 *	@return Goodsオブジェクト
	 *	@author Kotaro Nishida
	 */
	public Goods setParameter(String goodsName,
							 String imageUrl,
							 String amazonUrl,
							 String category,
							 Post post){
		this.setGoodsName(goodsName);
		this.setImageUrl(imageUrl);
		this.setAmazonUrl(amazonUrl);
		this.setCategory(category);
		this.setPost(post);
		return this;
	}

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
	public void setCategory(String category){
		this.category=category;
	}
	public String getCategory(){
		return this.category;
	}

	// 投稿
	public void setPost(Post post){
		this.post = post;
	}
	public Post getPost(){
		return post;
	}

	//ジャンルid
	public void setGenreId(String genreId){
		this.genreId = genreId;
	}
	public String getGenreId(){
		return this.genreId;
	}
}
