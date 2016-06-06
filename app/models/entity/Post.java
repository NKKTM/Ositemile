/*
 *	投稿クラス
 *	@author Kotaro Nishida
 */
package models.entity;

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
	private Long			_id;				// ID
	private String			_postComment;		// 投稿したユーザーのコメント

	@ManyToOne
	@JoinColumn(name = "_post")
	private Goods			_goods;				// 商品

	@ManyToOne
	@JoinColumn(name = "_post")
	private User			_user;				// ユーザー

	@OneToMany(mappedBy="_post",cascade = CascadeType.ALL)
	private List<Comment>	_comment;			// コメント

	//*****セッター・ゲッター*****

	// ID
	public void setId( Long id ){
		this._id = id;
	}
	public Long getId(){
		return _id;
	}

	// 商品
	public void setGoods(Goods goods){
		this._goods = goods;
	}
	public Goods getGoods(){
		return _goods;
	}

	// ユーザー
	public void setUser(User user){
		this._user = user;
	}
	public User getUser(){
		return _user;
	}

	// コメント
	public void setComment(List<Comment> comment){
		this._comment = comment;
	}
	public List<Comment> getComment(){
		return _comment;
	}

	// 投稿コメント
	public void setPostComment(String postComment){
		this._postComment = postComment;
	}
	public String getPostComment(){
		return _postComment;
	}

}
