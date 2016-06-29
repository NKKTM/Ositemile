/*
*  ログイン認証に関わるクラス
*  （Authenticatorクラスを継承してオーバーライドする必要がある）
*  @author Hatsune Kitajima
*/

package models.login;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security.Authenticator;
import models.service.*;
import models.entity.*;


public class Secured extends Authenticator {

	/*
	*  ログイン認証OKとする条件を設定するメソッド
	*  （この場合はセッションからloginIdが取得できればOKとしている）
	*  @author Hatsune Kitajima
	*/
	@Override
	public String getUsername(Context ctx){
		String loginId = ctx.session().get("loginId");
		User user = UserModelService.use().getUserByLoginId(loginId);
		if(user != null){
		//ユーザーがDBに存在するとき
			return user.getLoginId();
		}else{
		//ユーザーがDBに存在しないとき
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
		return redirect(controllers.routes.Application.login());
	}

}