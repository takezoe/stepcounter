package jp.sf.amateras.stepcounter.diffcount.diff;

import java.util.Arrays;
import java.util.List;

import difflib.ChangeDelta;
import difflib.Chunk;
import difflib.DeleteDelta;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.InsertDelta;
import difflib.Patch;

/**
 * 文字列の差分取得を行うためのDiffエンジンです。
 *
 * @author Naoki Takezoe
 */
public class DiffEngine {

	private IDiffHandler	handler;

	private List<String>	text1;

	private List<String>	text2;

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

		Patch rev = DiffUtils.diff(this.text1, this.text2);

		int count1 = 0;
		int count2 = 0;

		for (Delta delta : rev.getDeltas()) {
			Chunk orgChunk = delta.getOriginal();
			Chunk revChunk = delta.getRevised();

			while (count1 != orgChunk.getPosition()) {
				this.handler.match(this.text1.get(count1));
				count1++;
			}
			count1 = orgChunk.getPosition();
			count2 = revChunk.getPosition();

			if (delta instanceof InsertDelta) {
				while (count2 <= revChunk.last()) {
					this.handler.add(this.text2.get(count2));
					count2++;
				}

			} else if (delta instanceof DeleteDelta) {
				while (count1 <= orgChunk.last()) {
					this.handler.delete(this.text1.get(count1));
					count1++;
				}

			} else if (delta instanceof ChangeDelta) {
				while (count1 <= orgChunk.last()) {
					this.handler.delete(this.text1.get(count1));
					count1++;
				}
				while (count2 <= revChunk.last()) {
					this.handler.add(this.text2.get(count2));
					count2++;
				}

			}
			count1 = orgChunk.last() + 1;
			count2 = revChunk.last() + 1;
		}

		while (this.text2.size() > count2) {
			this.handler.match(this.text2.get(count2));
			count2++;
		}
	}

	/**
	 * 文字列を１行ごとに分割します。
	 *
	 * @param text 文字列
	 * @return １行ごとに分割された文字列
	 */
	private static List<String> splitLine(String text) {
		return Arrays.asList(text.split("\r?\n|\r"));
	}
}
