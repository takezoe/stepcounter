package jp.sf.amateras.diffcount.object;

/**
 * ファイルの変更状況を示すオブジェクトです。
 *
 * @author Naoki Takezoe
 */
public class FileObj extends AbstractObj {

	private int addedCount;

	@Override
	public int getAddedCount() {
		return addedCount;
	}

	public void setAddedCount(int addedCount) {
		this.addedCount = addedCount;
	}

	@Override
	protected String render(int nest) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<nest;i++){
			sb.append(" ");
		}
		sb.append(getName());
		sb.append("[").append(getStatus()).append("] ");
		sb.append(getAddedCount());
		return sb.toString();
	}

}
