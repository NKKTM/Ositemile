/*
 *	ユーザークラス
 *	@author Kotaro Nishida
 */
package models.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import play.db.ebean.Model;
import play.data.validation.Constraints;
import play.data.validation.Constraints.MaxLength;



@Entity
@Table(name = "userTable")
public class User extends Model{
	@Id
	private Long			id;			// ID
	@Constraints.Required(message="必須項目です。")
	private String			userName;	// ユーザー名
	private String			password;	// パスワード
	private String			loginId;	// ログインID
	@MaxLength(value = 150, message = "150文字以下で入力してください。")
	private String			profile;	// プロフィール
	@MaxLength(value = 30, message = "30文字以下で入力してください。")
	private String			department;	// 部署名
	private boolean			admin;		// 管理者権限を持つか否か

    @Column(columnDefinition="text")
	private String			imageEncData; //エンコーディング後の画像データ
	private String			imageName;	// 画像名
	@Lob
	private byte[]			imageData;	// 画像データー(バイナリー)

	@OneToMany(mappedBy="human",cascade = CascadeType.ALL)
	private List<Comment>	comment;	// コメント情報

	@OneToMany(mappedBy="human",cascade = CascadeType.ALL)
	private List<Post>		post;		// 投稿した情報

	@OneToMany(mappedBy="human",cascade = CascadeType.ALL)
	private List<Iine>		iine;		// いいねした情報

	/*
	 *	デフォルトコンストラクタ
	 *	@param なし
	 *	@return
	 *	@author Kotaro Nishida
	 */
	public User(){
		this.id = 0L;
		this.userName = "";
		this.password = "";
		this.loginId = "";
		this.profile = "よろしくお願いします。";
		this.department = "未設定";
		this.admin = false;
	}

	/*
	 *　コンストラクタ(パラメーター)
	 *	@param  String userName：ユーザー名
	 *			String password：パスワード
	 *			String loginId：ログインID
	 *	@return
	 *	@author Kotaro Nishida
	 */
	public User( String userName,String password,String loginId ,String profile,String department){
		this.userName = userName;
		this.password = password;
		this.loginId = loginId;
		this.admin = false;
		if(this.profile == ""){
			// プロフィールがない
			this.profile = "よろしくお願いします。";
		}else{
			// プロフィールがある
			this.profile = profile;
		}
		if( department == "" ){
			// 部署がない
			this.department = "未設定";
		}else{
			// 部署がある
			this.department = department;
		}
	}

	/*
	 *	パラメーターの設定
	 *	@param  String userName：ユーザー名
	 *			String password：パスワード
	 *			String loginId：ログインID
	 *	@return Userのオブジェクト
	 *	@author Kotaro Nishida
	 */
	public User setParameter( String userName,String password,String loginId ){
		this.setUserName(userName);
		this.setPassword(password);
		this.setLoginId(loginId);
		return this;
	}

	//*****セッター・ゲッター*****

	// ID
	public void setId(Long id){
		this.id = id;
	}
	public Long getId(){
		return id;
	}

	// ユーザー名
	public void setUserName(String name){
		this.userName = name;
	}
	public String getUserName(){
		return userName;
	}

	// パスワード
	public void setPassword(String password){
		this.password = password;
	}
	public String getPassword(){
		return password;
	}

	// ログイン
	public void setLoginId(String loginId){
		this.loginId = loginId;
	}
	public String getLoginId(){
		return loginId;
	}

	// コメント
	public void setComment(List<Comment> comment){
		this.comment = comment;
	}
	public List<Comment> getComment(){
		return comment;
	}

	// 投稿
	public void setIine( List<Iine> iine ){
		this.iine = iine;
	}
	public List<Iine> getIine(){
		return iine;
	}

	// いいね
	public void setPost( List<Post> post ){
		this.post = post;
	}
	public List<Post> getPost(){
		return post;
	}

	// プロフィール
	public void setProfile(String profile){
		this.profile = profile;
	}
	public String getProfile(){
		return profile;
	}

	// 部署
	public void setDepartment(String department){
		this.department = department;
	}
	public String getDepartment(){
		return department;
	}

	// 管理者権限
	public void setAdmin(boolean admin){
		this.admin = admin;
	}
	public boolean getAdmin(){
		return admin;
	}

	// エンコーディング後の画像データー
	public void setImageEncData(String imageEncData){
		this.imageEncData = "";
		this.imageEncData = imageEncData;

	}
	public String getImageEncData(){
		return this.imageEncData;
	}

	// 画像データー
	public void setImageData(byte[] image){
		this.imageData = image;
	}
	public byte[] getImageData(){
		return this.imageData;
	}

	// 画像名
	public void setImageName(String imageName){
		this.imageName = imageName;
	}
	public String getImageName(){
		return this.imageName;
	}

}
