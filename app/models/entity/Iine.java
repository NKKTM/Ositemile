/*
 *	商品クラス
 *	@author Hatsune Kitajima
 */
package models.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


import play.db.ebean.Model;

@Entity
public class Iine extends Model{

	@Id
	private Long			id;					// ID

	@ManyToOne
	@JoinColumn(name = "post")	
	private Post			post;				// 投稿情報

	@ManyToOne
	@JoinColumn(name = "user")	
	private User			user;				// いいねを押したユーザー情報	

	/*
	 *	デフォルトコンストラクタ
	 *	@param なし
	 *	@return
	 *	@author Hatsune Kitajima
	 */
	public Iine(){
		this.id = 0L;
	}

	/*
	 *	コンストラクタ(パラメーター)
	 *	@param	Post post：投稿情報
	 *	@return
	 *	@author Hatsune Kitajima
	 */
	public Iine(Post post,User user){
		this.post = post;
		this.user = user;
	}

	/*
	 *	パラメーターの設定
	 *	@param  Post post：投稿情報
	 *			User user：いいねを押したユーザー情報
	 *	@return Goodsオブジェクト
	 *	@author Kotaro Nishida
	 */
	public Iine setParameter(Post post,User user){
		this.setPost(post);
		this.setUser(user);
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

	// 投稿
	public void setPost(Post post){
		this.post = post;
	}
	public Post getPost(){
		return post;
	}

	// ユーザー情報
	public void setUser(User user){
		this.user = user;
	}
	public User getUser(){
		return user;
	}



}
