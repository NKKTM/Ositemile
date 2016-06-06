/*
 *	投稿クラス
 *	@author Kotaro Nishida
 */
package models.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.ebean.Model;

@Entity
public class Post  extends Model{

	@Id
	private Long			id;				// ID
	private String			postComment;		// 投稿したユーザーのコメント

	@ManyToOne
	@JoinColumn(name = "post")
	private Goods			goods;				// 商品

	@ManyToOne
	@JoinColumn(name = "post")
	private User			user;				// ユーザー

	@OneToMany(mappedBy="post",cascade = CascadeType.ALL)
	private List<Comment>	comment;			// コメント

	Date					date;				// 日付

	//*****セッター・ゲッター*****

	// ID
	public void setId( Long id ){
		this.id = id;
	}
	public Long getId(){
		return id;
	}

	// 商品
	public void setGoods(Goods goods){
		this.goods = goods;
	}
	public Goods getGoods(){
		return goods;
	}

	// ユーザー
	public void setUser(User user){
		this.user = user;
	}
	public User getUser(){
		return user;
	}

	// コメント
	public void setComment(List<Comment> comment){
		this.comment = comment;
	}
	public List<Comment> getComment(){
		return comment;
	}

	// 投稿コメント
	public void setPostComment(String postComment){
		this.postComment = postComment;
	}
	public String getPostComment(){
		return postComment;
	}

}
