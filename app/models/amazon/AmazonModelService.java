/*
 *	アマゾンのAPIを利用するためのクラス
 *	@author Hatsune Kitajima
 */
package models.amazon;

import java.util.ArrayList;
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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import models.entity.Goods;

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
	 * キーワード検索でxmlを取得し、大枠のxmlの情報を返す
	 * @param 　楽天apiのurl
	 * @return Element型 一番大きな外枠(root)のxml
	 * @author yuki kawakami
	 */

	public static Element getElement(String url) throws Exception {
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

	/*
	 * キーワードでヒットした商品のリストを返す
	 * @param　Element型 一番大きな外枠(root)のxml
	 * @return List<Goods>
	 * @author yuki kawakami
	 */
	public List<Goods> getSearchedGoodsList(Element elementRoot){
		//itemsリスト取得
    	NodeList localNodeList =
    			 ((Element) elementRoot.getElementsByTagName("Items").item(0)).getElementsByTagName("Item");
    	List<Goods> goodsList = new ArrayList<Goods>();

    	for (int i = 0; i < localNodeList.getLength(); i++) {
    		String imageUrl=null;
	      	 Element elementItem = (Element) localNodeList.item(i);
	      	 //itemNameを取得
	      	 Element elementItemName = (Element) elementItem.getElementsByTagName("itemName").item(0);
	      	 String itemName = elementItemName.getFirstChild().getNodeValue();
	       	 //itemUrlを取得
	       	 Element elementItemUrl = (Element) elementItem.getElementsByTagName("itemUrl").item(0);
	       	 String itemUrl = elementItemUrl.getFirstChild().getNodeValue();
	       	 //imageUrlの1個目を取得
	       	 Element elementImageUrl = (Element) elementItem.getElementsByTagName("imageUrl").item(0);
	       	 if(elementImageUrl!=null){
	       		 imageUrl = elementImageUrl.getFirstChild().getNodeValue();
	       		 imageUrl = imageUrl.replace("?_ex=64x64", "");	   }
	       	 //ジャンルid取得
	       	 Element elementGenreId = (Element) elementItem.getElementsByTagName("genreId").item(0);
	       	 String genreId =  elementGenreId.getFirstChild().getNodeValue();

	       	 Goods item = new Goods();
	       	 item.setGoodsName(itemName);
	       	 item.setAmazonUrl(itemUrl);
	       	 item.setImageUrl(imageUrl);
	       	 item.setGenreId(genreId);
	       	goodsList.add(item);
       	}
    	return goodsList;
	}

	/*
	 * ジャンル名をStringで返す
	 * @param　Element型 一番大きな外枠(root)のxml
	 * @return String　ジャンル名
	 * @author yuki kawakami
	 */

	public String getCategory(Element elementRoot){
		NodeList localNodeList =
				((Element) elementRoot.getElementsByTagName("parents").item(0)).getElementsByTagName("parent");
		String category = null;
		Element elementItem = (Element) localNodeList.item(0);
		//genreNameを取得
		Element elementGenreName = (Element) elementItem.getElementsByTagName("genreName").item(0);
		String itemName = elementGenreName.getFirstChild().getNodeValue();
		System.out.println(itemName);
		category = itemName;

		return category;
	}
}