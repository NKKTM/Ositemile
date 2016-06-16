/**
 *	投稿の更新フォーム
 *	@author Kotaro Nishida
 */
package models.form;

import javax.persistence.Column;

import play.data.validation.Constraints;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Pattern;

public class UpdatePostForm {
	@Constraints.Required(message="必須項目です。")
	@MaxLength(value = 150, message = "150文字以下で入力してください。")
	public String postTitle;

	@Column(columnDefinition="text")
	@Constraints.Required(message="必須項目です。")
	@MaxLength(value = 1000, message = "1000文字以下で入力してください。")
	public String postComment;
}
