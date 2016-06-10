package models.form;

import play.data.validation.*;

public class CommentForm {
	@Constraints.Required(message="必須項目です。")
	public String comment;
}
