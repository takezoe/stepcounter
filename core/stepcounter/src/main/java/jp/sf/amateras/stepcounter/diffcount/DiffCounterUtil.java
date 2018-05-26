package jp.sf.amateras.stepcounter.diffcount;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.sf.amateras.stepcounter.diffcount.object.AbstractDiffResult;
import jp.sf.amateras.stepcounter.diffcount.object.DiffFileResult;
import jp.sf.amateras.stepcounter.diffcount.object.DiffFolderResult;


/**
 * ユーティリティメソッドを提供します。
 *
 * @author Naoki Takezoe
 */
public class DiffCounterUtil {

	/**
	 * ファイルを無視するかどうかを判定します。
	 *
	 * @param file ファイル
	 * @return 無視する場合true、無視しない場合false
	 */
	public static boolean isIgnore(File file) {
		String name = file.getName();
		if (name.equals("CVS")) {
			return true;
		}
		if (name.equals(".svn")) {
			return true;
		}
		if (name.equals(".hg")) {
			return true;
		}
		if (name.equals(".git")) {
			return true;
		}
		return false;
	}

	public static String formatDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return format.format(date);
	}

	/**
	 * ソースから空行を削除します。
	 *
	 * @param source ソース
	 * @return ソースから空行を削除した文字列
	 */
	public static String removeEmptyLines(String source) {
		StringBuilder sb = new StringBuilder();
		String[] lines = DiffCounterUtil.split(source);
		for (String line : lines) {
			if (!line.matches("\\s*")) {
				sb.append(line).append("\n");
			}
		}

		return sb.toString();
	}

	/**
	 * ソースを行ごとに分割して配列で返却します。
	 *
	 * @param source ソース
	 * @return ソースを行ごとに分割した配列
	 */
	public static String[] split(String source) {
		List<String> lines = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (c == '\n') {
				lines.add(sb.toString());
				sb.setLength(0);
			} else {
				sb.append(c);
			}
		}

		if (sb.length() > 0) {
			lines.add(sb.toString());
		}

		return lines.toArray(new String[lines.size()]);
	}

	/**
	 * ファイルからソースを読み込みます。
	 * ソースの改行コードはLFに統一して返却します。
	 *
	 * @param file ソースファイル
	 * @param charset 文字コード
	 * @return ファイルから読み込んだ文字列
	 */
	public static String getSource(File file, String charset) {
		if (file == null) {
			return "";
		}

		try {
			FileInputStream in = new FileInputStream(file);
			int size = in.available();
			byte[] buf = new byte[size];
			in.read(buf);
			in.close();
			String source = new String(buf, charset);

			source = source.replaceAll("\r\n", "\n");
			source = source.replaceAll("\r", "\n");

			return source;

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

//	/**
//	 * ファイルの文字コードを取得します。
//	 *
//	 * @param file ファイル
//	 * @return ファイルの文字コード
//	 */
//	public static String getFileEncoding(File file) {
//		String encoding = null;;
//
//		// Eclipseプラグイン上から実行された場合は、ワークスペースの文字コードを取得
//		if (ResourcesPlugin.getPlugin() != null) {
//			IWorkspace workspace = ResourcesPlugin.getWorkspace();
//			IPath location = Path.fromOSString(file.getAbsolutePath());
//			IFile resource = workspace.getRoot().getFileForLocation(location);
//
//			if (resource != null) {
//				try {
//					encoding = resource.getCharset();
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//			}
//		}
//
//		if (encoding == null) {
//			encoding = System.getProperty("file.encoding");
//		}
//
//		return encoding;
//	}

	public static List<DiffFileResult> convertToList(
			DiffFolderResult folderResult) {
		return new ArrayList<DiffFileResult>(
				convertToMap(folderResult).values());
	}

	/**
	 * 指定された差分フォルダを、差分ファイルのリストに変換します。
	 *
	 * @param folderResult 差分フォルダ
	 * @return 差分ファイルのリスト
	 */
	private static Map<String, DiffFileResult> convertToMap(
			DiffFolderResult folderResult) {
		Map<String, DiffFileResult> map = new TreeMap<String, DiffFileResult>();

		List<AbstractDiffResult> children = folderResult.getChildren();
		for (AbstractDiffResult child : children) {
			if (child instanceof DiffFolderResult) {
				Map<String, DiffFileResult> childMap = convertToMap((DiffFolderResult)child);
				map.putAll(childMap);
			} else if (child instanceof DiffFileResult) {
				DiffFileResult fileResult = (DiffFileResult)child;
				map.put(fileResult.getPath(), fileResult);
			}
		}

		return map;
	}

}
