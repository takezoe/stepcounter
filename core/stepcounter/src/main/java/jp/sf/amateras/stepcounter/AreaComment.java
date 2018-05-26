package jp.sf.amateras.stepcounter;

/**
 * 複数行コメント
 */
public class AreaComment {
	
	private String start;
	private String end;
	
	/**
	 * 引数なしのコンストラクタ
	 */
	public AreaComment(){ }
	
	/**
	 * 開始文字列と終了文字列を指定してAreaCommentを生成します。
	 *
	 * @param start 開始文字列
	 * @param end   終了文字列
	 */
	public AreaComment(String start,String end){
		setStartString(start);
		setEndString(end);
	}
	
	/**
	 * コメントの開始文字列を設定します
	 *
	 * @param start 開始文字列
	 */
	public void setStartString(String start){
		this.start = start;
	}

	/**
	 * コメントの終了文字列を設定します
	 *
	 * @param end 終了文字列
	 */
	public void setEndString(String end){
		this.end = end;
	}
	
	/**
	 * コメントの開始文字列を取得します
	 *
	 * @return 開始文字列
	 */
	public String getStartString(){
		return this.start;
	}

	/**
	 * コメントの終了文字列を取得します
	 *
	 * @return 終了文字列
	 */
	public String getEndString(){
		return this.end;
	}
}