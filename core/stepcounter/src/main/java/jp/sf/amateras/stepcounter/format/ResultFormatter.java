package jp.sf.amateras.stepcounter.format;

import jp.sf.amateras.stepcounter.CountResult;

/**
 * カウント結果のフォーマッタ
 */
public interface ResultFormatter {

	public byte[] format(CountResult[] result);

}
