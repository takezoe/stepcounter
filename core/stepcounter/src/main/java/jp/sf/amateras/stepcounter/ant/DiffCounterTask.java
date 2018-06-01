package jp.sf.amateras.stepcounter.ant;

import java.io.File;
import java.io.FileOutputStream;

import jp.sf.amateras.stepcounter.diffcount.Main;
import jp.sf.amateras.stepcounter.diffcount.renderer.RendererFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


/**
 * 差分カウントを行うためのAntタスクです。
 *
 * @author Naoki Takezoe
 */
public class DiffCounterTask extends Task {

	private String srcdir = null;
	private String olddir = null;
	private String format = null;
	private String output = null;
	private String encoding = null;

	/**
	 * 差分測定を実行します。
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		// 必須パラメータのチェック
		if(RendererFactory.getRenderer(format) == null){
			throw new BuildException("format " + format + " is invalid!");
		}
		if(srcdir == null || srcdir.length() == 0){
			throw new BuildException("srcdir is required!");
		}
		if(!new File(srcdir).isDirectory()){
			throw new BuildException("srcdir '" + srcdir + "' is not directory!");
		}
		if(olddir == null || olddir.length() == 0){
			throw new BuildException("olddir is required!");
		}
		if(!new File(olddir).isDirectory()){
			throw new BuildException("olddir '" + olddir + "' is not directory!");
		}

		try {
			File basedir = getProject().getBaseDir();

			Main main = new Main();
			main.setFormat(format);
			if(output != null && output.length() != 0){
				main.setOutput(new FileOutputStream(output));
			}
			main.setEncoding(encoding);
			main.setSrcdir(new File(basedir, srcdir));
			main.setOlddir(new File(basedir, olddir));

			main.executeCount();

			if(output!=null && !output.equals("")){
				System.out.println(new File(output).getAbsolutePath() + "にカウント結果を出力しました。");
			}
		} catch(Throwable t){
			t.printStackTrace();
		}
	}

	/**
	 * 現在のソースディレクトリを指定します。
	 * @param srcdir 現在のソースディレクトリ
	 */
	public void setSrcdir(String srcdir) {
		this.srcdir = srcdir;
	}

	/**
	 * 過去のソースディレクトリを指定します。
	 * @param olddir 過去のソースディレクトリ
	 */
	public void setOlddir(String olddir) {
		this.olddir = olddir;
	}

	/**
	 * フォーマットを指定します。
	 * @param format フォーマット
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * 出力するファイルを指定します。
	 * @param output 出力するファイル
	 */
	public void setOutput(String output) {
		this.output = output;
	}

	/**
	 * ソースファイルの文字コードを指定します。
	 * @param encoding 文字コード
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

}
