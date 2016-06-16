/*
 *	投稿サービスクラス
 *	@author Kotaro Nishida
 */
package models.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import models.entity.Post;
import play.db.ebean.Model.Finder;


public class PostModelService {

	final static public Integer LIMIT = 12;  // １ページあたりの表示件数

	/*
	 *	投稿サービスの生成
	 *	@param なし
	 *	@return 投稿サービスのインスタンス
	 *	@author Kotaro Nishida
	 */
	public static PostModelService use(){
		return new PostModelService();
	}

	/*
	 *　保存
	 *	@param 保存したPostオブジェクト
	 *	@return 保存したPostオブジェクト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public Post save(Post post){
		if( post != null ){
			// postが存在している
			post.save();
			return post;
		}else{
			// postが存在していない
			return null;
		}
	}

	/*
	 *	投稿情報全件取得
	 *	@param なし
	 *	@return Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostList(){
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.orderBy("date desc").findList();
		return checkPost(postList);
	}

	/*
	 *	ページングのためのMaxページ取得
	 *	@param なし
	 *	@return MaxPage
	 *			失敗時：null
	 *	@author Hatsune Kitajima
	 */
	public int getMaxPage(String category){
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList;
		if(category.equals("ALL")){
		//カテゴリがALLの場合
			postList = find.orderBy("date desc").findList();
		}else{
		//カテゴリがそれ以外の場合
			postList = find.where().ilike("goods.category", category).findList();
		}
		int maxPage;
		if(postList.size()%LIMIT == 0){
			maxPage = postList.size()/LIMIT;
		}else{
			maxPage = postList.size()/LIMIT + 1;
		}
		System.out.println("maxPage："+maxPage);
		return maxPage;
	}

	/*
	 *	１ページあたりの投稿リストを取得
	 *	@param 取得したいページ数
	 *	@return Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostList(Integer pageNumber){
		Integer pageNum = (pageNumber - 1 < 0)? 0 : pageNumber - 1;
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.orderBy("date desc").findPagingList(LIMIT)
							.getPage(pageNum)
							.getList();
		return checkPost(postList);
	}

	/*
	 *	投稿IDで特定の投稿情報を取得
	 *	@param 投稿ID
	 *	@return 投稿情報
	 *			失敗時；null
	 *	@author Kotaro Nishida
	 */
	public Post getPostListById( Long postId ){
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		Post post = find.where().eq("id", postId).findList().get(0);
		return checkPost(post);
	}

	/*
	 *　タイトルで投稿リストを取得
	 *	@param String title : 投稿タイトル
	 *	@return Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostListByTitle(String title){
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.where().ilike("postTitle", '%'+title+'%').findList();
		return checkPost(postList);
	}

	/*
	 *	投稿コメントで投稿リストを取得
	 *	@param String comment : 投稿コメント
	 *	@return Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostListByComment(String comment){
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.where().ilike("postComment", '%'+comment+'%').findList();
		return checkPost(postList);
	}

	/*
	 *	商品名で投稿リストを取得
	 *	@param String goodsName: 商品名
	 *	@return Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostListByName(String goodsName){
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.where().ilike("goods.getGoodsName()", '%'+goodsName+'%').findList();
		return checkPost(postList);
	}

	/*
	 *	カテゴリで投稿リスト取得
	 *	@param String category: カテゴリー
	 *	@return Postのリスト
	 *			失敗時；null
	 *	@author Kotaro Nishida -> ページング仕様に変更 @author Hatsune Kitajima
	 */
	public List<Post> getPostListByCategory(Integer pageNumber, String categoryName){
		Integer pageNum = (pageNumber - 1 < 0)? 0 : pageNumber - 1;
		Finder<Long,Post> find = new Finder<Long ,Post>(Long.class,Post.class);
		List<Post> postListSize = find.all();		// データーベースに入っているリストサイズ用
		List<Post> postList = null;

		if( checkPost(postListSize) == null ){
			// データーベースにデーターがない
			return null;
		}
		// カテゴリーの中身を調べる
		postList = find.where().ilike("goods.category", categoryName)
							.orderBy("date desc")
							.findPagingList(LIMIT)
							.getPage(pageNum)
							.getList();
		return checkPost(postList);
	}

	/*
	 *	ユーザーのIdでリストを取得
	 *	@param Long userId : ユーザーのID
	 *	@return Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostListByUserId(Long userId){
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.where("human.id = " + userId).findList();
		return checkPost(postList);
	}

	/*
	 *	引数のリストがnullまたは空かどうかのチェック
	 *	@param Postのリスト
	 *	@return 成功時：Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public List<Post> checkPost(List<Post> postList){
		if(postList == null){
			// 中身がnull
			return null;
		}
		if(postList.isEmpty()){
			// 中身が空
			return null;
		}
		return postList;
	}

	/*
	 *	引数のオブジェクトがnullまたは空かどうかのチェック
	 *	@param Postのリスト
	 *	@return 成功時：Postのリスト
	 *			失敗時：null
	 *	@author Kotaro Nishida
	 */
	public Post checkPost(Post post){
		if(post == null){
			// 中身がnull
			return null;
		}
		return post;
	}

	/*
	 * ポストの日付表示に使うdateのStringを返す
	 * @param なし
	 * @return String　
	 * @author yuki kawakami
	 */
	public String getDateString() throws ParseException{
		TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
    	SimpleDateFormat nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        nowDate.setTimeZone(tz);
        String formatedDate = nowDate.format(new Date());
        Date date = nowDate.parse(formatedDate);
        String dateString = nowDate.format(date).toString();
        return dateString;
	}


	/*
	 * 文字列のサニタイズ
	 * @param String サニタイズしたい文字列
	 * @return String　サニタイズ後の文字列
	 * @author yuki kawakami
	 */
	public String sanitizeString(String s){
		s = s.replaceAll("&", "&amp;");
		s = s.replaceAll("\"", "&quot;");
		s = s.replaceAll("'", "&#39;");
		s = s.replaceAll("<", "&lt;");
		s = s.replaceAll(">", "&gt;");
		s = s.replaceAll("\n", "<br />");
		return s;
	}

	/*
	 *サニタイズされた文字列を元の文字列に戻す
	 *@param String サニタイズされた文字列
	 *@return String　元の文字列
	 *@author yuki kawakami
	 */
	public String reverseSanitize(String s){
		s = s.replaceAll("&amp;","&");
		s = s.replaceAll("&quot;","\"");
		s = s.replaceAll("&#39;","'");
		s = s.replaceAll("&lt;","<");
		s = s.replaceAll("&gt;",">");
		s = s.replaceAll("<br />","\n");
		return s;
	}

	/*
	 *トップページの投稿検索
	 *@param string キーワード
	 *@return String　list<Post>
	 *@author yuki kawakami
	 */

	public List<Post> searchPostByKeyword(String keyword,Integer pageNumber){
		Integer pageNum = (pageNumber - 1 < 0)? 0 : pageNumber - 1;
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.where("postTitle LIKE '%"+keyword+"%'"+" OR "
										+"postComment LIKE '%"+keyword+"%'"+" OR "
										+"human.userName LIKE '%"+keyword+"%'"+" OR "
										+"goods.goodsName LIKE '%"+keyword+"%'")
									.orderBy("date desc")
									.findPagingList(LIMIT)
									.getPage(pageNum)
									.getList();
		return postList;
	}

	/*
	 *	コメントの多い順の投稿リストを取得
	 *	@param Integer pageNumber : ページング
	 *	@return Postのリスト
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostCommentSort(Integer pageNumber){
		Integer pageNum = (pageNumber - 1 < 0)? 0 : pageNumber - 1;
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.orderBy("commentCnt desc").findPagingList(LIMIT).getPage(pageNum).getList();
		return postList;
	}

	/*
	 *	いいねの多い順に投稿リストを取得
	 *	@param Integer pageNumber : ページング
	 *	@return Postのリスト
	 *	@author Kotaro Nishida
	 */
	public List<Post> getPostIineSort(Integer pageNumber){
		Integer pageNum = (pageNumber - 1 < 0)? 0 : pageNumber - 1;
		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.orderBy("iineCnt desc").findPagingList(LIMIT).getPage(pageNum).getList();
		for(int i = 0; i < postList.size();i++){
			System.out.println("いいねカウント数："+postList.get(i).getIineCnt());
		}
		return postList;
	}

	/*
	 *	先週投稿されたpostのいいねの多い順に投稿リストを取得
	 *	@param なし
	 *	@return Postのリスト
	 *	@author yuki kawakami
	 */

	public List<Post> getIineRanking() throws ParseException{
		List<Timestamp> period = getPeriod();
		Timestamp firstDay = period.get(0);
		Timestamp lastDay = period.get(1);

		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.where( "'" + firstDay + "' < date"+" AND date < '"+ lastDay+"'").orderBy("iineCnt desc").findList();

		return postList;
	}


	/*
	 *	先週投稿されたpostのコメントの多い順に投稿リストを取得
	 *	@param なし
	 *	@return Postのリスト
	 *	@author yuki kawakami
	 */
	public List<Post> getCommentRanking() throws ParseException{
		List<Timestamp> period = getPeriod();
		Timestamp firstDay = period.get(0);
		Timestamp lastDay = period.get(1);

		Finder<Long, Post> find = new Finder<Long, Post>(Long.class, Post.class);
		List<Post> postList = find.where( "'" + firstDay + "' < date"+" AND date < '"+ lastDay+"'").orderBy("commentCnt desc").findList();

		return postList;
	}


	/*
	 * その日からの先週の期間を返す(ランキング用)
	 * @param なし
	 * @return Timestampのリスト (index0：先週の始まる日,index1：先週の終わりの日)
	 * @author yuki kawakami
	 */

	public List<Timestamp> getPeriod() throws ParseException{
		List<Timestamp> period = new ArrayList<Timestamp>();

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();

		SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy/MM/dd 00:00:00.000");

		int week = cal1.get(Calendar.DAY_OF_WEEK);

		String firstDayStr = null;
		String lastDayStr = null;
		Timestamp firstDay = null;
		Timestamp lastDay = null;
		switch (week){
			case 1:
				cal1.add(Calendar.DAY_OF_MONTH, -7);
				firstDayStr = fmt1.format(cal1.getTime());
				cal2.add(Calendar.DAY_OF_MONTH,0);
				lastDayStr = fmt1.format(cal2.getTime());
				period.add(new Timestamp(fmt1.parse(firstDayStr).getTime()));
				period.add(new Timestamp(fmt1.parse(lastDayStr).getTime()));
				System.out.println("最初の日:"+firstDay+" 最後の日:"+lastDay);
				break;
			case 2:
				cal1.add(Calendar.DAY_OF_MONTH, -8);
				firstDayStr = fmt1.format(cal1.getTime());
				cal2.add(Calendar.DAY_OF_MONTH,-1);
				lastDayStr = fmt1.format(cal2.getTime());
				period.add(new Timestamp(fmt1.parse(firstDayStr).getTime()));
				period.add(new Timestamp(fmt1.parse(lastDayStr).getTime()));
				System.out.println("最初の日:"+firstDay+" 最後の日:"+lastDay);
				break;
			case 3:
				cal1.add(Calendar.DAY_OF_MONTH, -9);
				firstDayStr = fmt1.format(cal1.getTime());
				cal2.add(Calendar.DAY_OF_MONTH,-2);
				lastDayStr = fmt1.format(cal2.getTime());
				period.add(new Timestamp(fmt1.parse(firstDayStr).getTime()));
				period.add(new Timestamp(fmt1.parse(lastDayStr).getTime()));
				System.out.println("最初の日:"+firstDay+" 最後の日:"+lastDay);
				break;
			case 4:
				cal1.add(Calendar.DAY_OF_MONTH, -10);
				firstDayStr = fmt1.format(cal1.getTime());
				cal2.add(Calendar.DAY_OF_MONTH,-3);
				lastDayStr = fmt1.format(cal2.getTime());
				period.add(new Timestamp(fmt1.parse(firstDayStr).getTime()));
				period.add(new Timestamp(fmt1.parse(lastDayStr).getTime()));
				System.out.println("最初の日:"+firstDay+" 最後の日:"+lastDay);
				break;
			case 5:
				cal1.add(Calendar.DAY_OF_MONTH, -11);
				firstDayStr = fmt1.format(cal1.getTime());
				cal2.add(Calendar.DAY_OF_MONTH,-4);
				lastDayStr = fmt1.format(cal2.getTime());
				period.add(new Timestamp(fmt1.parse(firstDayStr).getTime()));
				period.add(new Timestamp(fmt1.parse(lastDayStr).getTime()));
				System.out.println("最初の日:"+firstDay+" 最後の日:"+lastDay);
				break;
			case 6:
				cal1.add(Calendar.DAY_OF_MONTH, -12);
				firstDayStr = fmt1.format(cal1.getTime());
				cal2.add(Calendar.DAY_OF_MONTH,-5);
				lastDayStr = fmt1.format(cal2.getTime());
				period.add(new Timestamp(fmt1.parse(firstDayStr).getTime()));
				period.add(new Timestamp(fmt1.parse(lastDayStr).getTime()));
				System.out.println("最初の日:"+firstDay+" 最後の日:"+lastDay);
				break;
			case 7:
				cal1.add(Calendar.DAY_OF_MONTH, -13);
				firstDayStr = fmt1.format(cal1.getTime());
				cal2.add(Calendar.DAY_OF_MONTH,-6);
				period.add(new Timestamp(fmt1.parse(firstDayStr).getTime()));
				period.add(new Timestamp(fmt1.parse(lastDayStr).getTime()));
				System.out.println("最初の日:"+firstDay+" 最後の日:"+lastDay);
				break;
		}
		return period;
	}
}
