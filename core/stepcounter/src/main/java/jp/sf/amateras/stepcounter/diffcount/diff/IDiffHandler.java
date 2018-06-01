package jp.sf.amateras.stepcounter.diffcount.diff;

/**
 * Diffクラスから通知を受け取るためのハンドラのインターフェースです。
 *
 * @author Naoki Takezoe
 */
public interface IDiffHandler {

	/**
	 * 行が一致した場合に呼び出されます。
	 * 
	 * @param text 判定対象文字列。
	 */
	public void match(String text);

	/**
	 * 行が削除されていた場合に呼び出されます。 
	 *
	 * @param text 処理対象文字列。
	 */
	public void delete(String text);

	/**
	 * 行が追加されていた場合に呼び出されます。 
	 *
	 * @param text 追加したい文字列。
	 */
	public void add(String text);

}
