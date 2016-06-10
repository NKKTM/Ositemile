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
		if(user.getAdmin()){
			System.out.println(user.getUserName()+"には管理者権限があります");
			return loginId;
		}else{
			System.out.println(user.getUserName()+"には管理者権限がありません");
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