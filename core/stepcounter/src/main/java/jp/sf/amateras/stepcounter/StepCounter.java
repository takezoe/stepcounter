package jp.sf.amateras.stepcounter;

import java.io.*;

/** ステップカウンタのインターフェース */
public interface StepCounter {

	/**
	 * カウントします
	 *
	 * @param file 対象ファイル。
	 * @param encoding 文字エンコーディング。
	 * @return カウント結果。
	 * @throws IOException 入出力例外が発生した場合。
	 */
	public CountResult count(File file, String encoding) throws IOException;

	/**
	 * ファイルタイプを取得します
	 *
	 * @return ファイルタイプ。
	 */
	public String getFileType();

}