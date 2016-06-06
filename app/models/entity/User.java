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

@Entity
public class User extends Model{
	@Id
	private Long			_id;		// ID
	private String			_userName;	// ユーザー名
	private String			_passward;	// パスワード
	private String			_loginId;	// ログインID

	@OneToMany(mappedBy="_user",cascade = CascadeType.ALL)
	private List<Comment>	_comment;	// コメント情報

	@OneToMany(mappedBy="_user",cascade = CascadeType.ALL)
	private List<Post>		_post;		// 投稿した情報

	//*****セッター・ゲッター*****

	// ID
	public void setId(Long id){
		this._id = id;
	}
	public Long getId(){
		return _id;
	}

	// ユーザー名
	public void setUserName(String name){
		this._userName = name;
	}
	public String getUserName(){
		return _userName;
	}

	// パスワード
	public void setPassward(String passward){
		this._passward = passward;
	}
	public String getPassward(){
		return _passward;
	}

	// ログイン
	public void setLoginId(String loginId){
		this._loginId = loginId;
	}
	public String getLoiginId(){
		return _loginId;
	}

	// コメント
	public void setComment(List<Comment> comment){
		this._comment = comment;
	}
	public List<Comment> getComment(){
		return _comment;
	}

	// 投稿
	public void setPost( List<Post> post ){
		this._post = post;
	}
	public List<Post> getPost(){
		return _post;
	}
}
