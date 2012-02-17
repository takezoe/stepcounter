package jp.sf.amateras.stepcounter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import jp.sf.amateras.stepcounter.format.FormatterFactory;
import jp.sf.amateras.stepcounter.format.ResultFormatter;


/** コマンドラインからの起動クラス */
public class Main {

	private File[] files;
	private ResultFormatter formatter;
	private OutputStream output = System.out;
	private boolean showDirectory = false;

	/** 引数で指定したディレクトリからの階層を表示するか設定します */
	public void setShowDirectory(boolean showDirectory) {
		this.showDirectory = showDirectory;
	}

	/** ファイルをセットします */
	public void setFiles(File[] files){
		this.files = files;
	}

	/** フォーマッタをセットします。 */
	public void setFormatter(ResultFormatter formatter){
		this.formatter = formatter;
	}

	/** 出力ストリームを設定します。 */
	public void setOutput(OutputStream output){
		this.output = output;
	}

	/** カウントを実行します */
	public void executeCount() throws IOException {
		// フォーマッタが設定されていない場合はデフォルトを使用
		if(formatter == null){
			formatter = FormatterFactory.getFormatter("");
		}
		// １ファイル or １ディレクトリずつカウント
		ArrayList<CountResult> list = new ArrayList<CountResult>();
		for(int i=0;i<files.length;i++){
			CountResult[] results = count(files[i]);
			for(int j=0;j<results.length;j++){
				list.add(results[j]);
			}
		}
		CountResult[] results = (CountResult[])list.toArray(new CountResult[list.size()]);
		if (this.showDirectory) {
			for (CountResult result : results) {
				// 差分ディレクトリ付きのファイル名に上書きします。
				result.setFileName(getFileNameWithDir(result.getFile()));
			}
		}
		output.write(formatter.format(results));
		output.flush();
		if(output != System.out){
			output.close();
		}
	}

	/** １ファイルをカウント */
	private CountResult[] count(File file) throws IOException {
		if(file.isDirectory()){
			File[] files = file.listFiles();
			ArrayList<CountResult> list = new ArrayList<CountResult>();
			for(int i=0;i<files.length;i++){
				CountResult[] results = count(files[i]);
				for(int j=0;j<results.length;j++){
					list.add(results[j]);
				}
			}
			return (CountResult[])list.toArray(new CountResult[list.size()]);
		} else {
			StepCounter counter = StepCounterFactory.getCounter(file.getName());
			if(counter!=null){
				CountResult result = counter.count(file, Util.getFileEncoding(file));
				return new CountResult[]{result};
			} else {
				// 未対応の形式の場合は形式にnullを設定して返す
				return new CountResult[]{
					new CountResult(file, file.getName(), null, null, 0, 0, 0)
				};
			}
		}
	}

	/** ディレクトリ付きファイル名の出力形式を取得します。 */
	private String getFileNameWithDir(File file) throws IOException {
		if (file.isDirectory()) {
			return file.getName();
		}
		if (this.files == null || this.files.length == 0) {
			return file.getName();
		}
		// ファイルの正規パスを取得します。
		String filePath = file.getCanonicalPath();
		for (File f : this.files) {
			String parentPath = f.getCanonicalPath();
			if (filePath.contains(parentPath)) {
				// 引数の正規パスにファイルが含まれている場合、
				// 選択されたディレクトリからのパスとファイル名を返却します。
				StringBuilder builder = new StringBuilder();
				builder.append('/');
				builder.append(f.getName());
				builder.append(filePath.substring(parentPath.length()).replaceAll("\\\\", "/"));
				return builder.toString();
			}
		}
		return file.getName();
	}

	/** コマンドライン起動用メソッド */
	public static void main(String[] args) throws IOException {

		if(args==null || args.length==0){
			System.exit(0);
		}
		String format = null;
		String output = null;
		String encoding = null;
		String showDirectory = null;
		ArrayList<File> fileList = new ArrayList<File>();
		for(int i=0;i<args.length;i++){
			if(args[i].startsWith("-format=")){
				String[] dim = Util.split(args[i],"=");
				format = dim[1];
			} else if(args[i].startsWith("-output=")){
				String[] dim = Util.split(args[i],"=");
				output = dim[1];
			} else if(args[i].startsWith("-encoding=")){
				String[] dim = Util.split(args[i],"=");
				encoding = dim[1];
			} else if(args[i].startsWith("-showDirectory=")){
				String[] dim = Util.split(args[i],"=");
					showDirectory = dim[1];
			} else {
				fileList.add(new File(args[i]));
			}
		}

		Main main = new Main();
		main.setFiles((File[])fileList.toArray(new File[fileList.size()]));
		main.setFormatter(FormatterFactory.getFormatter(format));
		if(output != null && !output.equals("")){
			main.setOutput(new PrintStream(new FileOutputStream(new File(output))));
		}

		if(encoding != null){
//			encoding = System.getProperty("file.encoding");
			Util.setFileEncoding(encoding);
		}
		if ("true".equalsIgnoreCase(showDirectory)) {
			main.setShowDirectory(true);
		}

		main.executeCount();
	}

}