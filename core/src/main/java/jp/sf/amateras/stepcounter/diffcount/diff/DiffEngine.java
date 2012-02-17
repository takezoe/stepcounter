package jp.sf.amateras.stepcounter.diffcount.diff;

import org.apache.commons.jrcs.diff.AddDelta;
import org.apache.commons.jrcs.diff.ChangeDelta;
import org.apache.commons.jrcs.diff.DeleteDelta;
import org.apache.commons.jrcs.diff.Delta;
import org.apache.commons.jrcs.diff.Diff;
import org.apache.commons.jrcs.diff.DifferentiationFailedException;
import org.apache.commons.jrcs.diff.Revision;

/**
 * 文字列の差分取得を行うためのDiffエンジンです。
 *
 * @author Naoki Takezoe
 */
public class DiffEngine {

	private IDiffHandler	handler;

	private String[]		text1;

	private String[]		text2;

	/**
	 * コンストラクタ。
	 *
	 * @param handler ハンドラ
	 * @param text1   編集前の文字列（nullの場合は空文字列として扱います）
	 * @param text2   編集後の文字列（nullの場合は空文字列として扱います）
	 */
	public DiffEngine(IDiffHandler handler, String text1, String text2) {
		if (text1 == null) {
			text1 = "";
		}
		if (text2 == null) {
			text2 = "";
		}
		this.handler = handler;
		this.text1 = splitLine(text1);
		this.text2 = splitLine(text2);
	}

	/**
	 * 差分取得処理を実行します。
	 */
	public void doDiff() {

		try {
			Revision rev = Diff.diff(this.text1, this.text2);

			int count1 = 0;
			int count2 = 0;

			for (int i = 0; i < rev.size(); i++) {
				Delta delta = rev.getDelta(i);

				Range orgRange = new Range(delta.getOriginal().rangeString());
				Range revRange = new Range(delta.getRevised().rangeString());

				while (count1 != orgRange.getFrom() - 1) {
					this.handler.match(this.text1[count1]);
					count1++;
				}
				count1 = orgRange.getFrom() - 1;
				count2 = revRange.getFrom() - 1;

				if (delta instanceof AddDelta) {
					while (count2 != revRange.getTo()) {
						this.handler.add(this.text2[count2]);
						count2++;
					}

				} else if (delta instanceof DeleteDelta) {
					while (count1 != orgRange.getTo()) {
						this.handler.delete(this.text1[count1]);
						count1++;
					}

				} else if (delta instanceof ChangeDelta) {
					while (count1 != orgRange.getTo()) {
						this.handler.delete(this.text1[count1]);
						count1++;
					}
					while (count2 != revRange.getTo()) {
						this.handler.add(this.text2[count2]);
						count2++;
					}

				}
				count1 = orgRange.getTo();
				count2 = revRange.getTo();
			}

			while (this.text2.length > count2) {
				this.handler.match(this.text2[count2]);
				count2++;
			}
		} catch (DifferentiationFailedException ex) {
			throw new RuntimeException(ex);
		}
	}

	private class Range {
		private int	from;

		private int	to;

		public Range(String rangeString) {
			if (rangeString.indexOf(",") != -1) {
				String[] dim = rangeString.split(",");
				this.from = Integer.parseInt(dim[0]);
				this.to = Integer.parseInt(dim[1]);
			} else {
				this.from = Integer.parseInt(rangeString);
				this.to = Integer.parseInt(rangeString);
			}
		}

		public int getFrom() {
			return this.from;
		}

		public int getTo() {
			return this.to;
		}
	}

	/**
	 * 文字列を１行ごとに分割します。
	 *
	 * @param text 文字列
	 * @return １行ごとに分割された文字列
	 */
	private static String[] splitLine(String text) {
		String result = text;
		result = result.replaceAll("\r\n", "\n");
		result = result.replaceAll("\r", "\n");
		return result.split("\n");
	}
}
