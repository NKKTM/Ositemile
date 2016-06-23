package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Util {
	/*
	 * sqlで検索をかける時用のエスケープ
	 * @param エスケープをかけたいString
	 * @return エスケープ後のString　
	 * @author yuki kawakami
	 */
	public static String replaceString(String str){
		str = str.replace("%", "\\%");
		str = str.replace("_", "\\_");
		str = str.replace("'", "''");
		return str;
	}

	/*
	 * sqlで検索をかける時用のエスケープ
	 * @param なし
	 * @return 日本時間のdate型
	 * @author yuki kawakami
	 */
	public static Date getJpDate(){
		Date date = null;
		TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
    	SimpleDateFormat nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    	nowDate.setTimeZone(tz);
    	String formatedDate = nowDate.format(new Date());
    	try {
			date = nowDate.parse(formatedDate);
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
    	return date;
	}
}
