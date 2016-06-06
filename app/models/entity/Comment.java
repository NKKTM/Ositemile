/*
 *	コメントクラス
 *	@author Kotaro Nishida
 */
package models.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Comment {

	@Id
	private Long	_id;			// ID
	private String	_comment;		// コメント内容

	@ManyToOne
	@JoinColumn(name = "_comment")
	private User	_user;			// コメントしたユーザ情報

	@ManyToOne
	@JoinColumn(name = "_comment")
	private Post	_post;			// 投稿情報

	//*****セッター・ゲッター*****

	// ID
	public void setId(Long id){
		this._id = id;
	}
	public Long getId(){
		return _id;
	}

	// コメント内容
	public void setComment(String comment){
		this._comment = comment;
	}
	public String getComment(){
		return _comment;
	}

	// コメントしたユーザ情報
	public void setUser(User user){
		this._user = user;
	}
	public User getUser(){
		return _user;
	}

	// 投稿情報
}
