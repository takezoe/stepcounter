package jp.sf.amateras.stepcounter.format;

/**
 * フォーマッタのインスタンスを作成するためのファクトリクラス。
 */
public class FormatterFactory {

	/**
	 * フォーマッタのインスタンスを生成します。
	 *
	 * @param format フォーマット
	 * @return フォーマッタのインスタンス
	 */
	public static ResultFormatter getFormatter(String format){
		// nullの場合はデフォルトフォーマット
		if(format==null){
			return new DefaultFormatter();
		}
		String name = format.toLowerCase();
		// CSVフォーマット
		if(name.equals("csv")){
			return new CSVFormatter();
			
		// XMLフォーマット
		} else if(name.equals("xml")){
			return new XMLFormatter();
			
		// JSONフォーマット
		} else if(name.equals("json")){
			return new JSONFormatter();
			
		// Excelフォーマット
		} else if(name.equals("excel")){
			return new ExcelFormatter();

		// デフォルトフォーマット
		} else {
			return new DefaultFormatter();
		}
	}

}
