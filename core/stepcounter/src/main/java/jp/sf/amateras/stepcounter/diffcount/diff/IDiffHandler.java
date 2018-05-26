package jp.sf.amateras.stepcounter.diffcount.diff;

/**
 * Diffクラスから通知を受け取るためのハンドラのインターフェースです。
 *
 * @author Naoki Takezoe
 */
public interface IDiffHandler {

	/** 行が一致した場合に呼び出されます。 */
	public void match(String text);

	/** 行が削除されていた場合に呼び出されます。 */
	public void delete(String text);

	/** 行が追加されていた場合に呼び出されます。 */
	public void add(String text);

}
