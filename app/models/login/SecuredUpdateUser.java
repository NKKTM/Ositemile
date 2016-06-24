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


public class SecuredUpdateUser extends Authenticator {

	/*
	*  ログイン認証OKとする条件を設定するメソッド
	*  （この場合はセッションからloginIdが取得できればOKとしている）
	*  @author Hatsune Kitajima
	*/
	@Override
	public String getUsername(Context ctx){
		String loginId = ctx.session().get("loginId");
		Long userId = Long.valueOf(ctx.request().path().replaceAll("/myProfileEdit","")) - 932108L;
		System.out.println("urlから取れてきてるのは："+userId);

		User user = UserModelService.use().getUserByLoginId(loginId);
		System.out.println("user.getId::"+user.getId());
		if(user != null){
		//ユーザーがDBに存在するとき
			if(user.getId().equals(userId)){
				return user.getLoginId();
			}else{
				System.out.println("君はここを編集する資格をもっていない。帰れ。");
				return null;
			}
		}else{
		//ユーザーがDBに存在しないとき
			System.out.println("そんなユーザーはDBに存在しません！セッションも消しますね。");
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
		System.out.println("認証NG");
		String returnUrl = ctx.request().uri();
		if(returnUrl == null){
			returnUrl="/";
		}
		ctx.session().put("returnUrl", returnUrl);
		return redirect("/");
	}

}