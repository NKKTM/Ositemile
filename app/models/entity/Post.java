/*
 *	投稿クラス
 *	@author Kotaro Nishida
 */
package models.entity;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Column;

import play.data.validation.Constraints;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Pattern;
import play.db.ebean.Model;

@Entity
public class Post  extends Model{

	@Id
	private Long			id;					// ID
	@Constraints.Required(message="必須項目です。")
	@MaxLength(value = 150, message = "150文字以下で入力してください。")
	private String 			postTitle;			// 投稿タイトル

	@Column(columnDefinition="text")
	@Constraints.Required(message="必須項目です。")
	@MaxLength(value = 1000, message = "1000文字以下で入力してください。")
	private String			postComment;		// 投稿したユーザーのコメント

	@OneToOne
	private Goods			goods;				// 商品

	@ManyToOne
	@JoinColumn(name = "human")
	private User			human;				// ユーザー

	@OneToMany(mappedBy="post",cascade = CascadeType.ALL)
	private List<Comment>	comment;			// コメント

	@OneToMany(mappedBy="post",cascade = CascadeType.ALL)
	private List<Iine>	iine;			// コメント

	private String          dateStr;            //画面に表示する日付のString
	private Timestamp			date;				// 日付
	private int				commentCnt;			// コメント数
	private int				iineCnt;			// いいね数

	/*
	 *	デフォルトコンストラクタ
	 *	@param　なし
	 *	@return
	 *	@author Kotaro Nishida
	 */
	public Post(){
		this.id = 0L;
		this.postTitle ="";
		this.postComment = "";
		this.goods = null;
		this.human = null;
		this.comment = null;
		this.date = new Timestamp(models.Util.getJpDate().getTime());
	}

	/*
	 *	コンストラクタ(パラメーター)
	 *	@param String postTitle：投稿タイトル
	 *			String postComment：投稿コメント
	 *	@return
	 *	@author yuki kawakami
	 */
	public Post(String postTitle,
				String postComment){
		this.postTitle = postTitle;
		this.postComment = postComment;
		this.date =  new Timestamp(models.Util.getJpDate().getTime());
	}

	/*
	 *	コンストラクタ(パラメーター)
	 *	@param String postTitle：投稿タイトル
	 *			String postComment：投稿コメント
	 *			Goods goods：商品
	 *			User user：ユーザー
	 *	@return
	 *	@author Kotaro Nishida
	 */
	public Post(String postTitle,
				String postComment,
				Goods goods,
				User user){
		this.postTitle = postTitle;
		this.postComment = postComment;
		this.goods = goods;
		this.human = user;
		this.date =  new Timestamp(models.Util.getJpDate().getTime());
	}

	/*
	 *	パラメータの設定
	 *	@param	String postTitle：投稿タイトル
	 *			String postComment：投稿コメント
	 *			Goods goods：商品
	 *			User user：ユーザー
	 *	@return Postのオブジェクト
	 *	@author Kotaro Nishida
	 */
	public Post setParameter(String postTitle,
							 String postComment,
							 Goods goods,
							 User user){
		this.setPostTitle(postTitle);
		this.setPostComment(postComment);
		this.setGoods(goods);
		this.setUser(user);
		return this;
	}
	//*****セッター・ゲッター*****

	// ID
	public void setId( Long id ){
		this.id = id;
	}
	public Long getId(){
		return id;
	}

	// 商品
	public void setGoods(Goods goods){
		this.goods = goods;
	}
	public Goods getGoods(){
		return goods;
	}

	// ユーザー
	public void setUser(User user){
		this.human = user;
	}
	public User getUser(){
		return human;
	}

	// コメント
	public void setComment(List<Comment> comment){
		this.comment = comment;
	}
	public List<Comment> getComment(){
		return comment;
	}

	// いいね
	public void setIine(List<Iine> iine){
		this.iine = iine;
	}
	public List<Iine> getIine(){
		return iine;
	}

	// 投稿コメント
	public void setPostComment(String postComment){
		this.postComment = postComment;
	}
	public String getPostComment(){
		return postComment;
	}

	// タイトル
	public void setPostTitle(String postTitle){
		this.postTitle = postTitle;
	}
	public String getPostTitle(){
		return postTitle;
	}

	// 日付
	public void setDate(Timestamp date){
		this.date = date;
	}
	public Timestamp getDate(){
		return date;
	}

	//日付のString
	public void setDateStr(String dateStr){
		this.dateStr = dateStr;
	}
	public String getDateStr(){
		return this.dateStr;
	}

	// コメント数
	public void setCommentCnt(int commentCnt){
		this.commentCnt = commentCnt;
	}
	public int getCommentCnt(){
		return commentCnt;
	}

	// いいね数
	public void setIineCnt(int iineCnt){
		this.iineCnt = iineCnt;
	}
	public int getIineCnt(){
		return iineCnt;
	}
}
