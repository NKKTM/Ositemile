package models.form;

import play.data.validation.Constraints;

public class SearchItemForm {
	@Constraints.Required(message="できるだけ商品が特定できるキーワードを入力してください。")
	public String searchWord;
}
