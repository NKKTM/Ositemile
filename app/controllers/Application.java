package controllers;

import play.*;

import play.mvc.*;

import play.data.Form;
import static play.data.Form.*;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.w3c.dom.*;
import org.w3c.dom.Element;


import javax.xml.parsers.DocumentBuilderFactory;

import play.data.DynamicForm;

import views.html.*;
import views.html.login.*;
import views.html.post.*;

import models.entity.*;
import models.entity.Comment;
import models.form.*;
import models.login.*;
import models.service.CommentModelService;
import models.amazon.*;




public class Application extends Controller {

    // 定数
    // AmazonAPIにリクエストするURLの固定部分
    private static final String AMAZON_URL = "https://app.rakuten.co.jp/services/api/IchibaItem/Search/20140222?applicationId=1084889951156254811&format=xml&sort=-reviewCount&keyword=";


    public static Result index() {
        String loginId = session().get("loginId");
        System.out.println("loginId:"+loginId);
        return ok(index.render(loginId));
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
        Form<RegisterForm> registerForm = form(RegisterForm.class).bindFromRequest();
        if(!registerForm.hasErrors()){
        	//ユーザー情報がフォームから取得できた場合
            //DBにuserを登録
        	User user = new User();
            user.setUserName(registerForm.get().userName);
            user.setPassword(registerForm.get().password);
            user.setLoginId(registerForm.get().loginId);
            user.save();
            System.out.println("DB登録に成功しました！");
            //セッションにloginIdを登録
            session().clear();
            session("loginId", user.getLoiginId());
            return redirect("/");
        }else{
        	//ユーザー情報がフォームから取得できなかった場合
            System.out.println("DB登録に失敗しました！");
            return redirect("/");
        }
    }

    // ログイン認証
    public static Result authenticate() {
        Form<LoginForm> loginForm = form(LoginForm.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            //ログイン入力がフォームから取得できなかった場合
            return badRequest(login.render(loginForm));
        } else {
            //ログイン入力がフォームから取得できた場合
            session().clear();
            session("loginId", loginForm.get().loginId);
            String returnUrl = ctx().session().get("returnUrl");
            if(returnUrl == null || returnUrl.equals("") || returnUrl.equals(routes.Application.login().absoluteURL(request()))) {
                returnUrl = routes.Application.authenticateSuccess().absoluteURL(request());
            }
            return redirect(returnUrl);
        }
    }

    // 認証成功したらここへ
    @Security.Authenticated(Secured.class)
    public static Result authenticateSuccess() {
            System.out.println("認証成功");
            return redirect("/");
    }

    // ログアウト
    @Security.Authenticated(Secured.class)
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect("/");
    }

    // POSTするアイテムを検索する
    public static Result postSearchItem(){
        // アイテムを探すワードを取得
        String[] params = {"searchWord"};
        DynamicForm searchWord = Form.form();
        searchWord = searchWord.bindFromRequest(params);
        String searchWordStr = searchWord.data().get("searchWord").toString();
        // URLと結合
        String searchUrl = AMAZON_URL + searchWordStr;
        System.out.println("searchUrl："+searchUrl);
        // APIの処理を行う
        try{
            System.out.println("API処理"+AmazonModelService.use().http(searchUrl));
        }catch(Exception e){
            System.out.println(e);
        }
        return ok(postSearchItem.render(session().get("loginId")));
    }

    //***お試しテスト***商品情報をxmlから取得する
    public static Result testGetItemInfo() throws Exception{

    	String url = AMAZON_URL +"海辺のカフカ"+"&genreInformationFlag=1";
    	Element elementRoot=testGetElement(url);
    	//count要素取得
    	Element elementCount = (Element) elementRoot.getElementsByTagName("count").item(0);
    	String count = elementCount.getFirstChild().getNodeValue();
    	System.out.println("count:"+count);
    	//itemsリスト取得
    	NodeList localNodeList =
    			 ((Element) elementRoot.getElementsByTagName("Items").item(0)).getElementsByTagName("Item");

    	//itemName取得
    	for (int i = 0; i < localNodeList.getLength(); i++) {
    	 //itemを取得
    	 Element elementItem = (Element) localNodeList.item(i);
    	 //itemNameを取得
    	 Element elementItemName = (Element) elementItem.getElementsByTagName("itemName").item(0);
    	 String itemName = elementItemName.getFirstChild().getNodeValue();
    	 //itemUrlを取得
    	 Element elementItemUrl = (Element) elementItem.getElementsByTagName("itemUrl").item(0);
    	 String itemUrl = elementItemUrl.getFirstChild().getNodeValue();
    	 //imageUrlの1個目を取得
    	 Element elementImageUrl = (Element) elementItem.getElementsByTagName("imageUrl").item(0);
    	 String imageUrl = elementImageUrl.getFirstChild().getNodeValue();
    	 System.out.println("itemName"+i+":"+itemName);
    	 System.out.println("画像URL"+i+":"+imageUrl);
    	 System.out.println("itemURL"+i+":"+itemUrl);
    	}
    	return TODO;
    }

    //****お試しテストメソッド***エレメントを返す
    public static Element testGetElement(String url) throws Exception {

    	// リクエスト送信
		URL requestUrl = new URL(url);
		HttpURLConnection connection = (HttpURLConnection)requestUrl.openConnection();
		InputStream input = connection.getInputStream();
		//DOMを使うためのインスタンス取得
		Document document= DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);

		//root要素取得
		Element elementRoot = document.getDocumentElement();
		return elementRoot;
    }


    //ユーザーページ *中の処理未実装
    public static Result userPage(){
    	 String loginId = session().get("loginId");
         System.out.println("loginId:"+loginId);
         return ok(user_page.render(loginId));
    }

    // 商品ページ
    public static Result introduction(){
    	Form<CommentForm> commentForm = new Form(CommentForm.class);

    	// コメントデーターの参照
    	CommentModelService commnetService = CommentModelService.use();
    	List<Comment> commentList = commnetService.getCommnetList();
    	if( commentList != null ){
    		return ok(introduction.render( commentForm ));
    	}else{
    		return ok(introduction.render( commentForm ));
    	}


    }

    // コメント登録
    public static Result commentCreate(){
    	Form<CommentForm> commnetForm = form(CommentForm.class).bindFromRequest();
    	if( !commnetForm.hasErrors() ){
    		// エラーがない
    		// コメント登録
    		Comment comment = new Comment(commnetForm.get().comment,null,null);
    		CommentModelService.use().save(comment);

    		return redirect(controllers.routes.Application.introduction());
    	}else{
    	}
    	return null;
    }

}
