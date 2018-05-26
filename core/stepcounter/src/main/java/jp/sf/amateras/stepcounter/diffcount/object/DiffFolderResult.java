package jp.sf.amateras.stepcounter.diffcount.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.sf.amateras.stepcounter.diffcount.FileComparator;


/**
 * ディレクトリの変更情報を示すオブジェクトです。
 *
 * @author Naoki Takezoe
 */
public class DiffFolderResult extends AbstractDiffResult {

	private List<AbstractDiffResult>	children	= new ArrayList<AbstractDiffResult>();

	public DiffFolderResult(DiffFolderResult parent) {
		super(parent);
	}

	public DiffFolderResult(String name, DiffStatus status,
			DiffFolderResult parent) {
		super(name, status, parent);
	}

	public void addChild(AbstractDiffResult child) {
		if(child != null){
			this.children.add(child);
		}
	}

	public List<AbstractDiffResult> getChildren() {
		return this.children;
	}

	public List<AbstractDiffResult> getSortedChildren() {
		List<AbstractDiffResult> list = new ArrayList<AbstractDiffResult>(
				getChildren());
		Collections.sort(list, new FileComparator());
		return list;
	}

	@Override
	public DiffStatus getStatus() {
		DiffStatus status = super.getStatus();
		if (status == DiffStatus.REMOVED || status == DiffStatus.ADDED) {
			return status;
		}

		for (AbstractDiffResult obj : getChildren()) {
			DiffStatus childStatus = obj.getStatus();
			if (childStatus != DiffStatus.NONE) {
				return DiffStatus.MODIFIED;
			}
		}

		return DiffStatus.NONE;
	}

	@Override
	protected String render(int nest) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nest; i++) {
			sb.append(" ");
		}
		sb.append(getName()).append("/");

		if (getStatus() != null) {
			sb.append("[").append(getStatus()).append("]");
		}
		sb.append(" +").append(getAddCount());
		sb.append(" -").append(getDelCount()).append("\n");

		for (AbstractDiffResult obj : getSortedChildren()) {
			sb.append(obj.render(nest + 1)).append("\n");
		}

		// 末尾の改行を削除して返却
		return sb.toString().replaceFirst("\n$", "");
	}

	@Override
	public int getAddCount() {
		int addCount = 0;
		for (AbstractDiffResult obj : getChildren()) {
			addCount = addCount + obj.getAddCount();
		}
		return addCount;
	}

	@Override
	public int getDelCount() {
		int delCount = 0;
		for (AbstractDiffResult obj : getChildren()) {
			delCount = delCount + obj.getDelCount();
		}
		return delCount;
	}
}
