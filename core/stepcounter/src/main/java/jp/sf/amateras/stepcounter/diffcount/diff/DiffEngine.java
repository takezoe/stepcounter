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
 * ������̍����擾���s�����߂�Diff�G���W���ł��B
 *
 * @author Naoki Takezoe
 */
public class DiffEngine {

	private IDiffHandler	handler;

	private List<String>	text1;

	private List<String>	text2;

	/**
	 * �R���X�g���N�^�B
	 *
	 * @param handler �n���h��
	 * @param text1   �ҏW�O�̕�����inull�̏ꍇ�͋󕶎���Ƃ��Ĉ����܂��j
	 * @param text2   �ҏW��̕�����inull�̏ꍇ�͋󕶎���Ƃ��Ĉ����܂��j
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
	 * �����擾���������s���܂��B
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
	 * ��������P�s���Ƃɕ������܂��B
	 *
	 * @param text ������
	 * @return �P�s���Ƃɕ������ꂽ������
	 */
	private static List<String> splitLine(String text) {
		return Arrays.asList(text.split("\r?\n|\r"));
	}
}
