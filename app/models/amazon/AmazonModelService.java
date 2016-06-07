/*
 *	アマゾンのAPIを利用するためのクラス
 *	@author Hatsune Kitajima
 */
package models.amazon;

import java.util.List;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

public class AmazonModelService {
	/*
	 *	AmazonModelServiceの生成
	 *	@param なし
	 *	@return AmazonModelServiceのインスタンス
	 *	@author Hatsune Kitajima
	 */
	public static AmazonModelService use(){
		return new AmazonModelService();
	}

	/*
	 *	リクエストURLの結果を返すメソッド
	 *	@param url
	 *	@return ?????
	 *	@author Hatsune Kitajima
	 */
	public String http(String url) throws Exception {

    	// リクエスト送信
		URL requestUrl = new URL(url);
		HttpURLConnection connection = (HttpURLConnection)requestUrl.openConnection();
		InputStream input = connection.getInputStream();

    	// 結果取得
		BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

		String line;
		StringBuilder tmpResult = new StringBuilder();
		while( (line = reader.readLine()) != null) {
			tmpResult.append(line);
		}
		reader.close();
		return tmpResult.toString();
	}	
}