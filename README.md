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
<p>
/*<br/>
*	クラスの説明<br/>
*	@author 作者<br/>
*/<br/>
public void クラス名(){<br/>
	/*<br/>
	*	メソッドの説明<br/>
	*	@params 引数<br/>
	*	@return 戻り値<br/>
	*	@author 作者<br/>
	*/	<br/>
	public void メソッド名(){<br/>
		if(条件){<br/>
			//条件の説明<br/>
		}else{<br/>
			//elseの条件の説明<br/>
		}<br/>
	}<br/>
}<br/>
</p>

####クラス名
- AaaaAaaa（頭大文字＋区切り大文字）

####メソッド名
- aaaAaaaa（頭小文字＋区切り大文字）
- getCat（動詞からスタート）

####変数
- aaaAaaaa（頭小文字＋区切り大文字）
- catList,catNum（固有名詞を前にする）

####クラスのメンバ
- _aaaaaaa（アンダーバーをつける）

####まとめ
- 人に分かりやすいコメントアウトを加える
- 変数やメソッド,クラスはユニークで分かりやすい名前にする


##html,CSS
####id
- CSSではなるべく使わない！class属性だけで済ませる

####class命名
- aaa-aaa（ハイフンでつなぐ）

