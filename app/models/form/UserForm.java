/*
 *	ユーザーフォーム
 *	@author Kotaro Nishida
 */
package models.form;

import play.data.validation.Constraints;

import play.data.validation.Constraints.MaxLength;

public class UserForm {
	@Constraints.Required(message="必須項目です。")
	public String			userName;		// ユーザー名
	public String			password;		// パスワード
	public String			loginId;		// ログインID

	public boolean			admin;			// 管理者かどうか
	@MaxLength(value = 30, message = "30文字以下で入力してください。")
	public String			department;		// 部署
	@MaxLength(value = 150, message = "150文字以下で入力してください。")
	public String			profile;		// プロフィール
	public String			imageName;		// 画像名
	public byte[]			imageDataOld;	// 前回の画像データー
	public String			imageNameOld;	// 前回の画像名
	public String			encoding;		// 画像をエンコーディング

	/*
	 *	デフォルトコンストラクタ
	 *	@param なし
	 *	@return なし
	 *	@author Kotaro Nishida
	 */
	public UserForm(){

	}

	/*
	 *	パラメーター設定
	 *	@param  String userName : ユーザー名
	 *			String profile : プロフィール
	 *			String password : パスワード
	 *			String loginId : ログインID
	 *			boolean admin : 管理者かどうか
	 *			String department : 部署
	 *			String imageData : 画像データー
	 *			String imageName : 画像名
	 *			byte[] imageDataOld : 前回の画像データー
	 *			String imageNameOld : 前回の画像名
	 *	@return なし
	 *	@author Kotaro Nishida
	 */
	public void setUserForm(String userName,
					String password,
					String loginId,
					boolean admin,
					String profile,
					String department,
					String imageName,
					byte[] imageDataOld,
					String imageNameOld,
					String encoding){
		this.userName = userName;
		this.password = password;
		this.loginId = loginId;
		this.admin = admin;
		this.profile = profile;
		this.department = department;
		this.imageName = imageName;
		this.imageDataOld = imageDataOld;
		this.imageNameOld = imageNameOld;
		this.encoding = encoding;
	}


}