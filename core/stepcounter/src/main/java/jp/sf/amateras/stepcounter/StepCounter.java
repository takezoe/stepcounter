package jp.sf.amateras.stepcounter;

import java.io.*;

/** ステップカウンタのインターフェース */
public interface StepCounter {

	/** カウントします */
	public CountResult count(File file, String encoding) throws IOException;

	/** ファイルタイプを取得します */
	public String getFileType();

}