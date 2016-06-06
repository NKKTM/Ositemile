/*
 *	ユーザークラス
 *	@author Kotaro Nishida
 */
package models.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.db.ebean.Model;

import play.db.ebean.*;


@Entity
public class User extends Model{
	@Id
	private Long			id;			// ID
	private String			userName;	// ユーザー名
	private String			password;	// パスワード
	private String			loginId;	// ログインID

	@OneToMany(mappedBy="user",cascade = CascadeType.ALL)
	private List<Comment>	comment;	// コメント情報

	@OneToMany(mappedBy="user",cascade = CascadeType.ALL)
	private List<Post>		post;		// 投稿した情報

	//*****セッター・ゲッター*****

	// ID
	public void setId(Long id){
		this.id = id;
	}
	public Long getId(){
		return id;
	}

	// ユーザー名
	public void setUserName(String name){
		this.userName = name;
	}
	public String getUserName(){
		return userName;
	}

	// パスワード
	public void setPassword(String passward){
		this.password = passward;
	}
	public String getPassword(){
		return password;
	}

	// ログイン
	public void setLoginId(String loginId){
		this.loginId = loginId;
	}
	public String getLoiginId(){
		return loginId;
	}

	// コメント
	public void setComment(List<Comment> comment){
		this.comment = comment;
	}
	public List<Comment> getComment(){
		return comment;
	}

	// 投稿
	public void setPost( List<Post> post ){
		this.post = post;
	}
	public List<Post> getPost(){
		return post;
	}
}
