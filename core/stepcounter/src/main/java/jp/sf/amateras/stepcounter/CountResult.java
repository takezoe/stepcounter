package jp.sf.amateras.stepcounter;

import java.io.File;

/** カウント結果（１ファイル） */
public class CountResult {

	private File file;
	private String fileName;
	private String fileType;
	private String category;
	private long step;
	private long non;
	private long comment;

	/** 引数なしのコンストラクタ */
	public CountResult(){ }


	public CountResult(File file, String fileName,String fileType,String category,long step,long non,long comment){
		setFileName(fileName);
		setFileType(fileType);
		setStep(step);
		setNon(non);
		setComment(comment);
		setCategory(category);
		setFile(file);
	}


	/** ファイル名を設定します */
	public void setFileName(String fileName){
		this.fileName = fileName;
	}

	/** ファイルの種類を設定します */
	public void setFileType(String fileType){
		this.fileType = fileType;
	}

	/** 実行ステップ数を設定します */
	public void setStep(long step){
		this.step = step;
	}

	/** 空行数を設定します */
	public void setNon(long non){
		this.non = non;
	}

	/** コメント行数を設定します */
	public void setComment(long comment){
		this.comment = comment;
	}

	/** ファイル名を取得します */
	public String getFileName(){
		return this.fileName;
	}

	/** ファイルの種類を取得します */
	public String getFileType(){
		return this.fileType;
	}

	/** 実行ステップ数を取得します */
	public long getStep(){
		return this.step;
	}

	/** 空行数を取得します */
	public long getNon(){
		return this.non;
	}

	/** コメント行数を取得します */
	public long getComment(){
		return this.comment;
	}

	/** カウント結果を文字列で取得します */
	public String getResultString(){
		return toString();
	}

	/** ファイルのカテゴリを取得します */
	public String getCategory() {
		return category;
	}

	/** ファイルのカテゴリを設定します */
	public void setCategory(String category) {
		this.category = category;
	}

	/** ファイルオブジェクトを設定します */
	public void setFile(File file) {
		this.file = file;
	}

	/** ファイルオブジェクトを返却します */
	public File getFile() {
		return this.file;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(getFileName()).append(" ");
		sb.append("実行:").append(Long.toString(getStep())).append(" ");
		sb.append("空行:").append(Long.toString(getNon())).append(" ");
		sb.append("コメント:").append(Long.toString(getComment()));
		return sb.toString();
	}
}