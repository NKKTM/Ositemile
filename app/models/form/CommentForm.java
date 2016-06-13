package models.form;

import play.data.validation.*;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Pattern;

public class CommentForm {
	@Constraints.Required(message="必須項目です。")
	@Pattern(value = "^[-0-9一-龠a-zA-Zァ-ヶぁ-ゞー 　!！?？（）()^_/>＞＜。、「」『』％&＆＝=〜￥¥#＃@＠*:：＊+＋”＿.]+$", message = "使用できるのは英,ひら,全角カナ,漢字のみです。")
	@MaxLength(value = 150, message = "150文字以下で入力してください。")
	public String comment;
}
