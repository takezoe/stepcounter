package jp.sf.amateras.diffcount;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {

	public static String[] split(String source){
		List<String> lines = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();

		for(int i=0;i<source.length();i++){
			char c = source.charAt(i);
			if(c == '\n'){
				lines.add(sb.toString());
				sb.setLength(0);
			} else {
				sb.append(c);
			}
		}

		if(sb.length() > 0){
			lines.add(sb.toString());
		}

		return lines.toArray(new String[lines.size()]);
	}

	/**
	 * ファイルからソースを読み込みます。
	 * ソースの改行コードはLFに統一して返却します。
	 * <p>
	 * TODO 文字コードは指定できないとまずくない？
	 *
	 * @param file ソースファイル
	 * @return ファイルから読み込んだ文字列
	 */
	public static String getSource(File file){
		if(file == null){
			return "";
		}
		try {
			FileInputStream in = new FileInputStream(file);
			int size = in.available();
			byte[] buf = new byte[size];
			in.read(buf);
			in.close();
			String source = new String(buf);

			source = source.replaceAll("\r\n", "\n");
			source = source.replaceAll("\r", "\n");

			return source;

		} catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}

}
