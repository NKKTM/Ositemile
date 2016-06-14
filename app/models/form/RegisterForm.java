/*
 *	新規登録のフォーム
 *	@author Hatsune kitajima
 */
package models.form;

import models.service.UserModelService;
import play.data.validation.Constraints;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Pattern;

public class RegisterForm {
	@Constraints.Required(message="必須項目です。")
	@Pattern(value = "^[一-龠a-zA-Zァ-ヶぁ-ゞー]+$", message = "使用できるのは英,ひら,全角カナ,漢字のみです。")
	@MaxLength(value = 16, message = "16文字以下で入力してください。")
	public String userName;
	@Constraints.Required(message="必須項目です")
	@Pattern(value = "^[a-zA-Z0-9]+$", message = "半角英数字のみで入力してください。")
	@MinLength(value = 4, message = "4文字以上入力してください。")
	@MaxLength(value = 16, message = "16文字以下で入力してください。")
	public String password;
	@Constraints.Required(message="必須項目です")
	@Pattern(value = "^[a-zA-Z0-9]+$", message = "半角英数字のみで入力してください。")
	@MinLength(value = 4, message = "4文字以上入力してください。")
	@MaxLength(value = 16, message = "16文字以下で入力してください。")
	public String loginId;
}