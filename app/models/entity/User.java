/*
 *	ユーザークラス
 *	@author Kotaro Nishida
 */
package models.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class User {
	@Id
	private Long			_id;		// ID
	private String			_name;		// ユーザー名
	private String			_passward;	// パスワード
	private String			_loginId;	// ログインID

	@OneToMany(mappedBy="_user",cascade = CascadeType.ALL)
	private List<Comment>	_comment;	// コメント情報
	// 投稿した情報

	//*****セッター・ゲッター*****

	// ID
	public void setId(Long id){
		this._id = id;
	}
	public Long getId(){
		return _id;
	}

	// ユーザー名
	public void setUser(String name){
		this._name = name;
	}
	public String getUser(){
		return _name;
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

	// 投稿
}
