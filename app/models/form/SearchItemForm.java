package models.form;

import play.data.validation.Constraints;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Pattern;

public class SearchItemForm {
	@Constraints.Required(message="できるだけ商品が特定できるキーワードを入力してください。")
	@MinLength(value = 2, message = "2文字以上入力してください。")
	@MaxLength(value = 50, message = "50文字以下で入力してください。")
	public String searchWord;
}
