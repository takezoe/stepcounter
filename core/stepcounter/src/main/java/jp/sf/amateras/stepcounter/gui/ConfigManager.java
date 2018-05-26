package jp.sf.amateras.stepcounter.gui;

import java.io.*;
import java.util.*;
import java.text.*;

/**
 * 設定ファイルを読み込み、更新するための汎用クラス。
 *
 * @author  Naoki Takezoe
 * @version 1.0
 */
public class ConfigManager {

	private File file;
	private HashMap<String, Vector<String>> map;

	/**
	 * コンストラクタ
	 *
	 * @param fileName プロパティファイルの名前
	 */
	public ConfigManager(String fileName) {
		this(new File(fileName));
	}

	/**
	 * コンストラクタ
	 *
	 * @param file プロパティファイル
	 */
	public ConfigManager(File file){
		this.file = file;
		try {
			this.read();
		} catch(Exception e){
			this.map = new HashMap<String, Vector<String>>();
		}
	}


	/**
	 * プロパティを設定します。
	 * 同名のパラメータが存在する場合、上書きされます。
	 *
	 * @param key   プロパティキー
	 * @param value プロパティの値
	 */
	public void setProperty(String key,String value){
		String[] values = {value};
		setProperty(key,values);
	}

	/**
	 * インデックスプロパティを設定します。
	 *
	 * @param key    プロパティキー
	 * @param values プロパティの値
	 */
	public void setProperty(String key,String[] values){
		Vector<String> vec = new Vector<String>();
		for(int i=0;i<values.length;i++){
			vec.add(values[i]);
		}
		this.map.put(key,vec);
	}


	/**
	 * プロパティを取得します。
	 *
	 * @param key プロパティのキー
	 * @return    プロパティの値
	 */
	public String getProperty(String key){
		String[] values = getPropertyValues(key);
		if(values!=null && values.length==0){ return null; }
		return values[0];
	}

	/**
	 * インデックスプロパティを取得します。
	 *
	 * @param key プロパティのキー
	 * @return    プロパティの値
	 */
	public String[] getPropertyValues(String key){
		if(this.map.get(key)==null){ return null; }
		Vector<String> vec = this.map.get(key);
		String[] dim = new String[vec.size()];
		for(int i=0;i<vec.size();i++){
			dim[i] = (String)vec.get(i);
		}
		return dim;
	}

	/**
	 * プロパティファイルを読み込みます。
	 * インスタンスの生成時に内部的に呼び出されます。
	 */
	private synchronized void read() throws IOException {
		this.map = new HashMap<String, Vector<String>>();
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(this.file));
		while((line=reader.readLine())!=null){
			// トリム
			line.trim();
			// #で始まる行はコメント
			if(!line.startsWith("#")){
				String[] dim = StringUtility.split(line,"=");
				if(dim.length>=2){
					String key   = dim[0].trim();
					String value = dim[1].trim();

					Vector<String> values;
					if(this.map.get(key) == null){
						values = new Vector<String>();
					} else {
						values = this.map.get(key);
					}

					values.add(value);
					map.put(key,values);
				}
			}
		}
		reader.close();
	}

	/**
	 * プロパティファイルを更新（存在しなければ作成）します。
	 */
	public synchronized void save() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(this.file));
		// ヘッダを出力
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		writer.write("# Date: " + formatter.format(new Date()));
		writer.newLine();

		// キーセットを取得
		Set<String> set = this.map.keySet();
		Iterator<String> iterator = set.iterator();
		// 書き出し
		while(iterator.hasNext()){
			String key    = (String)iterator.next();
			Vector<String> values = this.map.get(key);
			for(int i=0;i<values.size();i++){
				writer.write(key + "=" + (String)values.get(i));
				writer.newLine();
			}
		}
		// フラッシュ
		writer.flush();
		writer.close();
	}
}