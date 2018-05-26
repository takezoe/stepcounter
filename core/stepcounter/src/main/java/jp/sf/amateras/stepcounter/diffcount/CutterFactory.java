package jp.sf.amateras.stepcounter.diffcount;

import java.io.File;

import jp.sf.amateras.stepcounter.StepCounter;
import jp.sf.amateras.stepcounter.StepCounterFactory;


/**
 * {@link Cutter}のファクトリです。
 * ファイルの拡張子から適切な{@link Cutter}の実装を返却します。
 *
 * @author Naoki Takezoe
 */
public class CutterFactory {

	/**
	 * ファイルの拡張子から適切な{@link Cutter}の実装を返却します。
	 * 対応する{@link Cutter}の実装が存在しない場合はnullを返します。
	 *
	 * @param file ファイル
	 * @return ファイルに対応する{@link Cutter}の実装
	 */
	public static Cutter getCutter(File file) {
		StepCounter counter = StepCounterFactory.getCounter(file.getName());
		if(counter != null && counter instanceof Cutter){
			return (Cutter) counter;
		}
		return null;
	}

	/**
	 * ファイルの種別を返却します。
	 * サポートされていないファイル形式の場合は "Unknown" を返却します。
	 *
	 * @param file ファイル
	 * @return ファイル種別
	 */
	public static String getFileType(File file){
		Cutter cutter = getCutter(file);
		if(cutter == null){
			return "Unknown";
		}
		return cutter.getFileType();
	}

}
