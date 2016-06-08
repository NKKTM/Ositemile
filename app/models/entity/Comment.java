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
	@JoinColumn(name = "user")
	private User	user;			// コメントしたユーザ情報

	@ManyToOne
	@JoinColumn(name = "post")
	private Post	post;			// 投稿情報

	Date			date;			// 日付

	/*
	 *	デフォルトコンストラクタ
	 *	@param なし
	 *	@return
	 *	@author Kotaro Nishida
	 */
	public Comment(){
		this.id = 0L;
		this.comment = "";
		this.user = null;
		this.post = null;
		this.date = new Date();
	}

	/*
	 *	コンストラクタ(パラメーター)
	 *	@param  String comment： コメントの内容
	 *			User user：コメントしたユーザー情報
	 *			Post post：投稿情報
	 *	@return
	 *	@author Kotaro Nishida
	 */
	public Comment(String comment, User user, Post post){
		this.comment = comment;
		this.user = user;
		this.post = post;
		this.date = new Date();
	}

	/*
	 *	パラメーターの設定
	 *	@param  String comment： コメントの内容
	 *			User user：コメントしたユーザー情報
	 *			Post post：投稿情報
	 *			：
	 *	@return Commentオブジェクト
	 *	@author Kotaro Nishida
	 */
	public Comment setParameter( String comment, User user, Post post ){
		this.setComment(comment);
		this.setUser(user);
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
	public void setPost(Post post){
		this.post = post;
	}
	public Post getPost(){
		return post;
	}
}
