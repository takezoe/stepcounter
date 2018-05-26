package jp.sf.amateras.stepcounter;

import java.io.File;

/**
 * ファイルの文字コード検出を拡張するためのインターフェースです。
 *
 * @see Util#setFileEncodingDetector(FileEncodingDetector)
 * @see Util#getFileEncoding(File)
 *
 * @author Naoki Takezoe
 */
public interface FileEncodingDetector {

	/**
	 * ファイルの文字コードを返します。検出できない場合はnullを返します。
	 *
	 * @param file ファイル
	 * @return 文字コード
	 */
	public String getEncoding(File file);

}
