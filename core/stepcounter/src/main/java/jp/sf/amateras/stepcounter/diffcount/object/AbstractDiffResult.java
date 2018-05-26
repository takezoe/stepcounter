package jp.sf.amateras.stepcounter.diffcount.object;

/**
 * ファイル、ディレクトリの変更情報を示すオブジェクトの抽象基底クラスです。
 *
 * @author Naoki Takezoe
 */
public abstract class AbstractDiffResult {

	private DiffStatus			status;

	private String				name;

	private DiffFolderResult	parent;

	public AbstractDiffResult(DiffFolderResult parent) {
		this.parent = parent;
	}

	public AbstractDiffResult(String name, DiffStatus status,
			DiffFolderResult parent) {
		this.name = name;
		this.status = status;
		this.parent = parent;
	}

	public DiffFolderResult getParent() {
		return this.parent;
	}

	public DiffStatus getStatus() {
		return this.status;
	}

	public void setStatus(DiffStatus status) {
		this.status = status;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		DiffFolderResult tempParent = this.parent;
		StringBuilder pathBuilder = new StringBuilder();
		while (tempParent != null) {
			pathBuilder.insert(0, "/");
			pathBuilder.insert(0, tempParent.getName());

			tempParent = tempParent.getParent();
		}
		pathBuilder.append(this.name);

		return pathBuilder.toString();
	}

	protected abstract String render(int nest);

	public abstract int getAddCount();

	public abstract int getDelCount();

	@Override
	public String toString() {
		return render(0);
	}

	// TODO 親から出したほうがよいですねぇ
	public String getClassName() {
		AbstractDiffResult obj = this;

		StringBuilder sb = new StringBuilder();
		sb.append(obj.hashCode());

		while (true) {
			obj = obj.getParent();
			if (obj != null) {
				sb.insert(0, obj.hashCode() + "_");
			} else {
				break;
			}
		}

		return sb.toString();
	}
}
