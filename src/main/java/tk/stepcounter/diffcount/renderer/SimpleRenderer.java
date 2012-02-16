package tk.stepcounter.diffcount.renderer;

import java.util.Date;

import tk.stepcounter.diffcount.DiffCounterUtil;
import tk.stepcounter.diffcount.object.DiffFolderResult;

/**
 * 差分カウントの結果をテキスト形式でレンダリングします。
 *
 * @author Naoki Takezoe
 */
public class SimpleRenderer implements Renderer {

	public byte[] render(DiffFolderResult root) {
		StringBuilder sb = new StringBuilder();
		sb.append("実行時刻：").append(DiffCounterUtil.formatDate(new Date())).append("\n");
		sb.append("--\n");
		sb.append(root.toString());

		return sb.toString().getBytes();
	}

}
