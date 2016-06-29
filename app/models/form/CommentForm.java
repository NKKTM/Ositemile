/*
 *	コメント入力フォーム
 *	@author Kotaro Nishida
 */
package models.form;

import play.data.validation.*;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Pattern;

public class CommentForm {
	@Constraints.Required(message="必須項目です。")
	@MaxLength(value = 150, message = "150文字以下で入力してください。")
	public String comment;
}
