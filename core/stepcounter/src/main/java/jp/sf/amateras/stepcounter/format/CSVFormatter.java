package jp.sf.amateras.stepcounter.format;

import jp.sf.amateras.stepcounter.CountResult;

/**
 * カウント結果をCSV形式でフォーマットします。
 *
 * TODO 国際化
 */
public class CSVFormatter implements ResultFormatter {

//	private String fileHeader    = "ファイル";
//	private String stepHeader    = "実行";
//	private String nonHeader     = "空行";
//	private String commentHeader = "コメント";
//	private String typeHeader    = "種類";
//	private String sumHeader     = "合計";

	public byte[] format(CountResult[] results){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<results.length;i++){
			CountResult result = results[i];
			// 未対応の形式をフォーマット
			if(result.getFileType()==null){
				sb.append(result.getFileName());
				sb.append(",");
				sb.append("未対応");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append(",");
				sb.append("\n");
			// 正常にカウントされたものをフォーマット
			} else {
				sb.append(result.getFileName());
				sb.append(",");
				sb.append(result.getFileType());
				sb.append(",");
				sb.append(result.getCategory());
				sb.append(",");
				sb.append(result.getStep());
				sb.append(",");
				sb.append(result.getNon());
				sb.append(",");
				sb.append(result.getComment());
				sb.append(",");
				sb.append(result.getStep()+result.getNon()+result.getComment());
				sb.append("\n");
			}
		}
		return sb.toString().getBytes();
	}
}
