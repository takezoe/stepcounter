package tk.stepcounter.diffcount;

import java.io.File;
import java.util.ResourceBundle;

import tk.stepcounter.StepCounter;
import tk.stepcounter.StepCounterFactory;

/**
 * {@link Cutter}のファクトリです。
 * ファイルの拡張子から適切な{@link Cutter}の実装を返却します。
 *
 * @author Naoki Takezoe
 */
public class CutterFactory {

	private static final String NOT_SUPPORTED = ResourceBundle.getBundle(
			"tk.eclipse.plugin.stepcounter.StepCounterPluginResources").getString("StepCountView.notSupported");

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
			return NOT_SUPPORTED;
		}
		return cutter.getFileType();
	}

}
