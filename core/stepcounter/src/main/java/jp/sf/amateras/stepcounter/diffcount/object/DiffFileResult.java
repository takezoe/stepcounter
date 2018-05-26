package jp.sf.amateras.stepcounter.diffcount.object;

/**
 * ファイルの変更状況を示すオブジェクトです。
 *
 * @author Naoki Takezoe
 */
public class DiffFileResult extends AbstractDiffResult {

	private String	fileType;

	private int		addCount;

	private int		delCount;

	private String	category;

	public DiffFileResult(DiffFolderResult parent) {
		super(parent);
	}

	public DiffFileResult(String name, DiffStatus status,
			DiffFolderResult parent) {
		super(name, status, parent);
	}

	public String getCategory() {
		if (category == null) {
			return "";
		}
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFileType() {
		return this.fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	@Override
	public int getAddCount() {
		return this.addCount;
	}

	public void setAddCount(int addCount) {
		this.addCount = addCount;
	}

	@Override
	public int getDelCount() {
		return this.delCount;
	}

	public void setDelCount(int delCount) {
		this.delCount = delCount;
	}

	@Override
	protected String render(int nest) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nest; i++) {
			sb.append(" ");
		}
		sb.append(getName());
		sb.append("[").append(getStatus()).append("]");
		sb.append(" +").append(getAddCount());
		sb.append(" -").append(getDelCount());
		return sb.toString();
	}
}
