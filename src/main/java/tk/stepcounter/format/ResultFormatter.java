package tk.stepcounter.format;

import tk.stepcounter.CountResult;

/**
 * カウント結果のフォーマッタ
 */
public interface ResultFormatter {

	public byte[] format(CountResult[] result);

}
