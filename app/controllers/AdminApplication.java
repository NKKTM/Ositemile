package controllers;

import play.*;



import play.mvc.*;

import views.html.*;
import views.html.post.*;
import views.html.admin.*;

import java.text.SimpleDateFormat;
import java.util.*;
import play.data.Form;
import static play.data.Form.*;

import play.data.DynamicForm;

import models.*;
import models.entity.*;
import models.service.*;

import com.avaje.ebean.Ebean;

import models.form.admin.*;


public class AdminApplication extends Controller {

	// commnetリスト
	@Security.Authenticated(models.login.SecuredAdmin.class)
	public static Result commentList(){

		return TODO;
	}

	// commnet検索
	@Security.Authenticated(models.login.SecuredAdmin.class)
	public static Result commentSearch(){
		return TODO;
	}
}
