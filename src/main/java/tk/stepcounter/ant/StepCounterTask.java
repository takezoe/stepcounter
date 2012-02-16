/*
 * Created on 2003/06/19
 *
 * Ant用カスタムタスク
 * コンパイルするときはクラスパスにant.jarを指定してください。
 * でもAntでコンパイルすれば、特に指定しなくてもOK
 */

package tk.stepcounter.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import tk.stepcounter.Main;
import tk.stepcounter.format.FormatterFactory;

/**
 * ステップカウンタ（{@link tk.stepcounter.Main}）を実行するAntタスクです。
 * 入れ子のfilesetタグでファイルを指定します。
 *
 * @author sawat
 */
public class StepCounterTask extends Task {

	private List<FileSet> filesets = new ArrayList<FileSet>();
	private String format = null;
	private String output = null;
	private String encoding = null;
	private List<Path> showDirectoryList = new LinkedList<Path>();

	/**
	 * ステップ数測定を実行します。
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {

		List<File> files = new ArrayList<File>();

		// ファイルセットから該当するファイルを取り出す
		for (int i = 0; i < filesets.size(); i++) {
			FileSet fs = (FileSet) filesets.get(i);
			DirectoryScanner scanner = fs.getDirectoryScanner(getProject());
			scanner.scan();
			File baseDir = scanner.getBasedir();

			String[] includeFile = scanner.getIncludedFiles();
			for (int j = 0; j < includeFile.length; j++) {
				files.add(new File(baseDir, includeFile[j]));
			}
		}

		// 実行
		try {
			Main main = new Main();
			main.setFormatter(FormatterFactory.getFormatter(format));
			if (showDirectoryList.size() > 0) {
				List<File> dirs = new LinkedList<File>();

				for (Path s : showDirectoryList) {
					String[] path = s.list();
					for (int i = 0; i < path.length; i++) {
						dirs.add(new File(path[i]));
					}
				}
				main.setFiles((File[]) dirs.toArray(new File[dirs.size()]));
				main.setShowDirectory(true);
			} else {
				main.setFiles((File[]) files.toArray(new File[files.size()]));
			}

			if(output != null && !output.equals("")){
				main.setOutput(new FileOutputStream(new File(output)));
			}
			System.out.println(files.size() + "ファイル");

			if(encoding == null || encoding.length() == 0){
				encoding = System.getProperty("file.encoding");
			}
			main.executeCount(encoding);
			if(output!=null && !output.equals("")){
				System.out.println(new File(output).getAbsolutePath() + "にカウント結果を出力しました。");
			}
		} catch (IOException e) {
			throw new BuildException(e);
		}

	}
	/**
	 * ファイルセットを追加します。
	 * @param fileset ファイルセット
	 */
	public void addFileSet(FileSet fileset) {
		this.filesets.add(fileset);
	}

	/**
	 * フォーマットを指定します。
	 * @param format フォーマット
	 */
	public void setFormat(String format){
		this.format = format;
	}

	/**
	 * 出力するファイルを指定します。
	 * @param file 出力するファイル
	 */
	public void setOutput(String output){
		this.output = output;
	}

	/**
	 * ソースファイルの文字コードを指定します。
	 * @param encoding 文字コード
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * ディレクトリまたはファイルの出力形式を追加します。
	 *
	 * @param showDirectory 出力形式
	 */
	public Path createShowDirectory() {
		Path showDirectory = new Path(getProject());
		this.showDirectoryList.add(showDirectory);
		return showDirectory;
	}
}
