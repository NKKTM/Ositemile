package controllers;

import play.*;
import play.mvc.*;

import views.html.*;
import views.html.login.*;

import play.data.Form;
import static play.data.Form.*;

import models.entity.*;
import models.form.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    //ログイン画面
    public static Result login() {
        Form<LoginForm> loginForm = new Form(LoginForm.class);
        return ok(login.render(loginForm));
    }

    //新規登録画面
    public static Result register() {
        Form<RegisterForm> registerForm = new Form(RegisterForm.class);
        return ok(register.render(registerForm));
    }

    //新規ユーザーをDBに登録
    public static Result registerDB(){
        Form<User> userForm = form(User.class).bindFromRequest();
        if(!userForm.hasErrors()){
        	//ユーザー情報がフォームから取得できた場合
            User user = userForm.get();
            user.save();
            System.out.println("DB登録に成功しました！");
            return redirect("/");            
        }else{
        	//ユーザー情報がフォームから取得できなかった場合
            System.out.println("DB登録に失敗しました！");
            return redirect("/");             	
        }
    }

}
