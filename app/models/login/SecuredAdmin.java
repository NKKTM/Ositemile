/*
*  管理者ログイン認証に関わるクラス
*  （Authenticatorクラスを継承してオーバーライドする必要がある）
*  @author Hatsune Kitajima
*/

package models.login;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security.Authenticator;
import models.entity.*;
import models.service.UserModelService;

public class SecuredAdmin extends Authenticator {

	/*
	*  管理者ログイン認証OKとする条件を設定するメソッド
	*  （この場合はセッションのloginIdのユーザーが管理権限を持っていればOKとしている）
	*  @author Hatsune Kitajima
	*/
	@Override
	public String getUsername(Context ctx){
		String loginId = ctx.session().get("loginId");
		User user = UserModelService.use().getUserByLoginId(loginId);
		if(user != null){
		//ユーザーが存在する場合
			if(user.getAdmin()){
			//ユーザーが管理者権限を持っている場合
				return loginId;
			}else{
			//ユーザーが管理者権限を持っていない場合
				return null;
			}
		}else{
		//ユーザーが存在しない場合
        	ctx.session().clear();
			return null;
		}
	}


	/*
	*  ログイン認証NGとする場合の処理を設定するメソッド
	*  （この場合は最終的にlogin()に飛ばすように設定している）
	*  @author Hatsune Kitajima
	*/
	@Override
	public Result onUnauthorized(Context ctx){
		String returnUrl = ctx.request().uri();
		if(returnUrl == null){
			returnUrl="/";
		}
		ctx.session().put("returnUrl", returnUrl);
		return redirect("/");
	}

}