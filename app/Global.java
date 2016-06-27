/*
 *  エラーページの遷移を実行するクラス
 *
 *  @author Hatsune Kitajima
 */

import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.libs.F.*;

import static play.mvc.Results.*;

public class Global extends GlobalSettings {

    // エラーが起きた場合
    public Promise<SimpleResult> onError(RequestHeader request, Throwable t) {
        return Promise.<SimpleResult>pure(internalServerError(
            views.html.errorpage.render()
        ));
    }

    // ページが見つからなかった場合
    public Promise<SimpleResult> onHandlerNotFound(RequestHeader request) {
        return Promise.<SimpleResult>pure(notFound(
            views.html.errorpage.render()
        ));
    }    

    // 存在しないURLを打ち込まれた場合
    public Promise<SimpleResult> onBadRequest(RequestHeader request, String error) {
        return Promise.<SimpleResult>pure(badRequest(
            views.html.errorpage.render()
        ));
    }    

}