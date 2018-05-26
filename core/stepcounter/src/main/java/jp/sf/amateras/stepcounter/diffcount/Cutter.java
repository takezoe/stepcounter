package jp.sf.amateras.stepcounter.diffcount;

/**
 * ソースコードからコメントや空行など、
 * ステップ数のカウント時に不要な部分を取り除くためのカッターのインターフェースです。
 *
 * @author Naoki Takezoe
 */
public interface Cutter {

	/**
	 * ソースコードから不要な部分を取り除きます。
	 *
	 * @param source ソース
	 * @return 不要な部分を取り除いた文字列
	 */
	public DiffSource cut(String source);

	/** ファイルタイプを取得します */
	public String getFileType();

}
