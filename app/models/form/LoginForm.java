/*
 *	ログインのフォーム
 *	@author Hatsune kitajima
 */
package models.form;

import play.data.validation.*;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Pattern;
import play.db.ebean.*;
import java.security.NoSuchAlgorithmException;
import models.entity.User;

public class LoginForm {

	@Constraints.Required
	@Pattern(value = "^[a-zA-Z0-9]+$", message = "半角英数字のみで入力してください。")
	@MinLength(value = 4, message = "4文字以上入力してください。")
	@MaxLength(value = 16, message = "16文字以下で入力してください。")
	public String loginId;

	@Constraints.Required
	@Pattern(value = "^[a-zA-Z0-9]+$", message = "半角英数字のみで入力してください。")
	@MinLength(value = 4, message = "4文字以上入力してください。")
	@MaxLength(value = 16, message = "16文字以下で入力してください。")
	public String password;


	/*
	 *	ユーザ/パスワードの組み合わせでDBを参照し、正しいかどうかを判断してくれるメソッド
	 *	このクラスがFormのコンストラクタに渡されると、実際にフォームをPOSTする前に、このvalidate()メソッドを実行し、ユーザ/パスワードの組み合わせが正しいかどうかを判断してくれる
	 *	@author Hatsune kitajima
	 */
	public String validate() throws NoSuchAlgorithmException{
		if(authenticate(loginId,password)==null){
			return "Invalid user or password";
		}
		return null;
	}

	/*
	 *	loginIdとpasswordの組み合わせに合うユーザーが存在するかを返してくれるメソッド
	 *	@author Hatsune kitajima
	 */
	public static User authenticate(String loginId, String password) throws java.security.NoSuchAlgorithmException{
		Model.Finder<Long, User> find = new Model.Finder<Long, User>(Long.class, User.class);
			return find.where().eq("loginId", loginId).eq("password", password).findUnique();
	}


}