package jp.sf.amateras.stepcounter;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;

/**
 * 各種ユーティリティメソッドを提供するクラス
 */
public class Util {

	private static String fileEncoding = null;
	private static FileEncodingDetector fileEncodingDetector = null;

	/**
	 * 文字列を指定文字列で分割し、配列で返却します。
	 *
	 * @param str 文字列
	 * @param del 区切り文字列
	 * @return 分割された文字列を格納した配列
	 */
	public static String[] split(String str,String del){
		ArrayList<String> list = new ArrayList<String>();
		int pos   = 0;
		int index = 0;
		while((index=str.indexOf(del,pos))!=-1){
			list.add(str.substring(pos,index));
			pos = index + del.length();
		}
		list.add(str.substring(pos,str.length()));
		return (String[])list.toArray(new String[list.size()]);
	}

	/**
	 * 渡された文字列を指定エンコーディングの指定バイト数で先頭から切り出す。
	 * カタカナの判定は正しく行うことができない。
	 *
	 * @param   s    切り出し対象文字列
	 * @param   cnt  切り出しバイト数
	 * @return  結果文字列
	 */
	public static String substring(String str,int length){
		String resultStr = null;
		int zenCnt      = 0;
//		int kisuuFlg    = 0;
		int loopCnt     = length;
		byte[] resBytes = new byte[length];
		byte[] bytes    = str.getBytes();
		// 指定バイト数以下の場合はそのまま返却
		if(bytes.length <= length) {
			return str;
		}
		for (int i=0; i < length; i++) {
			if (bytes[i] < 0) {
				// bytes[i]の8ビット目が立っている(全角)
				zenCnt ++;
			}
		}
		// 全角バイトの数が奇数の場合
		if(zenCnt % 2 == 1) {
			loopCnt--;
		}
		for(int i=0; i < loopCnt ; i++) {
			resBytes[i] = bytes[i];
		}
		resultStr = new String(resBytes);
		return resultStr;
	}

	/**
	 * 引数で渡した文字列のバイト長を返します。
	 *
	 * @param str 文字列
	 * @return バイト長
	 */
	public static int getByteLength(String str){
		try {
			byte[] bytes = str.getBytes();
			return bytes.length;
		} catch(Exception ex){
			return str.getBytes().length;
		}
	}

	/**
	 * HTML/XMLの特殊文字を実態参照に変換します。
	 *
	 * @param str 文字列
	 * @return 変換後の文字列
	 */
	public static String escapeXML(String str){
		str.replaceAll("&" ,"&amp;");
		str.replaceAll("<" ,"&gt;");
		str.replaceAll(">" ,"&lt;");
		str.replaceAll("\"","&quot;");
		return str;
	}

	/**
	 * ストリームを強制的にクローズします。
	 *
	 * @param closeable ストリーム
	 */
	public static void close(Closeable closeable){
		if(closeable != null){
			try {
				closeable.close();
			} catch(Exception ex){
				;
			}
		}
	}

	public static void setFileEncodingDetector(FileEncodingDetector detector){
		fileEncodingDetector = detector;
	}

	public static void setFileEncoding(String encoding){
		fileEncoding = encoding;
	}

	public static String getFileEncoding(File file){
		if(fileEncoding != null){
			return fileEncoding;
		}

		if(fileEncodingDetector != null){
			String encoding = fileEncodingDetector.getEncoding(file);
			if(encoding != null){
				return encoding;
			}
		}

		return System.getProperty("file.encoding");
	}
}
