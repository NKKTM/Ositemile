#heroku実行URL
- https://shielded-tundra-21071.herokuapp.com/  
予備herokuのurl  
- https://young-wave-75666.herokuapp.com/

#楽天API情報
- アプリID/デベロッパーID(applicationId/developerId)：1084889951156254811
- application_secret：99ecdfa01f08c4cafde96a71f6ec83e91adec9f6
- アフィリエイトID(affiliateId)：14fa0111.d141fce2.14fa0112.d325820f
- コールバック許可ドメイン：mysterious-bastion-67235.herokuapp.com

#コード規約

##Play

####View
- フォルダをしっかり分ける（要相談）

####Model
- aaaaModelService（パッケージで末尾は統一,entityはしなくて良い）
- entityにはメソッドは書かない（getter,setterのみ）
- entityの処理メソッドはModelServiceに書く
- entityのメンバ変数はprivate

####Controller
- aaaaController（名前＋Controller）


##Java

####コメントの書き方
<pre><code>
/\*
\*	クラスの説明
\*	@author 作者
\*/
public void クラス名(){
	/\*
	\*	メソッドの説明
	\*	@params 引数
	\*	@return 戻り値
	\*	@author 作者
	\*/
	public void メソッド名(){
		if(条件){
			//条件の説明
		}else{
			//elseの条件の説明
		}
	}
}
</code></pre>

####クラス名
- AaaaAaaa（頭大文字＋区切り大文字）

####メソッド名
- aaaAaaaa（頭小文字＋区切り大文字）
- getCat（動詞からスタート）

####変数
- aaaAaaaa（頭小文字＋区切り大文字）
- catList,catNum（固有名詞を前にする）

####まとめ
- 人に分かりやすいコメントアウトを加える
- 変数やメソッド,クラスはユニークで分かりやすい名前にする


##html,CSS
####id
- CSSではなるべく使わない！class属性だけで済ませる

####class命名
- aaa-aaa（ハイフンでつなぐ）

