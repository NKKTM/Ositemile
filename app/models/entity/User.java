/*
 *	ユーザークラス
 *	@author Kotaro Nishida
 */
package models.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
	@Id
	private Long _id;			// ID
	private String _name;		// ユーザー名
	private String _passward;	// パスワード
	private String _loginId;	// ログインID
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
