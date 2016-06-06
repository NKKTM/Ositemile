/*
 *	コメントクラス
 *	@author Kotaro Nishida
 */
package models.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;

@Entity
public class Comment  extends Model{

	@Id
	private Long	id;				// ID
	private String	comment;		// コメント内容

	@ManyToOne
	@JoinColumn(name = "comment")
	private User	user;			// コメントしたユーザ情報

	@ManyToOne
	@JoinColumn(name = "comment")
	private Post	post;			// 投稿情報

	Date			date;			// 日付

	//*****セッター・ゲッター*****

	// ID
	public void setId(Long id){
		this.id = id;
	}
	public Long getId(){
		return id;
	}

	// コメント内容
	public void setComment(String comment){
		this.comment = comment;
	}
	public String getComment(){
		return comment;
	}

	// コメントしたユーザ情報
	public void setUser(User user){
		this.user = user;
	}
	public User getUser(){
		return user;
	}

	// 投稿情報
}
