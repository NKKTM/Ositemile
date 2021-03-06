/*
 *	アマゾンのAPIを利用するためのクラス
 *	@author Hatsune Kitajima
 */
package models.amazon;

import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import models.entity.Goods;


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
	 * 			失敗時 null
	 * @author yuki kawakami
	 */

	public Element getElement(String url)  {
    	// リクエスト送信
		URL requestUrl = null;
		try {
			requestUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection)requestUrl.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		InputStream input = null;
		try {
			input = connection.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		//DOMを使うためのインスタンス取得
		Document document = null;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}

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
		List<Goods> goodsList = new ArrayList<Goods>();
		//itemsリスト取得
    	NodeList localNodeList =
    			 ((Element) elementRoot.getElementsByTagName("Items").item(0)).getElementsByTagName("Item");


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
		String category = "その他";
		try {
		NodeList localNodeList =
				((Element) elementRoot.getElementsByTagName("parents").item(0)).getElementsByTagName("parent");
		Element elementItem = (Element) localNodeList.item(0);
		//genreNameを取得
		Element elementGenreName = (Element) elementItem.getElementsByTagName("genreName").item(0);
		String itemName = elementGenreName.getFirstChild().getNodeValue();
		category = itemName;
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		return category;
	}
}