package jp.sf.amateras.stepcounter.diffcount.renderer.gui;

import jp.sf.amateras.stepcounter.diffcount.object.AbstractDiffResult;
import jp.sf.amateras.stepcounter.diffcount.object.DiffFileResult;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;


public class DiffCountTreeTableModel extends DefaultTreeTableModel {

	private static final int	INDEX_NAME		= 0;

	private static final int	INDEX_STATUS	= 1;

	private static final int	INDEX_ADD		= 2;

	private static final int	INDEX_DEL		= 3;

	@Override
	public boolean isLeaf(Object node) {
		AbstractDiffResult obj = (AbstractDiffResult)((DefaultMutableTreeTableNode)node).getUserObject();
		return (obj instanceof DiffFileResult);
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case INDEX_NAME:
			return "名前";
		case INDEX_STATUS:
			return "ステータス";
		case INDEX_ADD:
			return "追加行数";
		case INDEX_DEL:
			return "削除行数";
		default:
			return null;
		}
	}

	@Override
	public Object getValueAt(Object node, int column) {
		AbstractDiffResult obj = (AbstractDiffResult)((DefaultMutableTreeTableNode)node).getUserObject();

		switch (column) {
		case INDEX_NAME:
			return obj.getName();
		case INDEX_STATUS:
			return obj.getStatus();
		case INDEX_ADD:
			return obj.getAddCount();
		case INDEX_DEL:
			return obj.getDelCount();
		default:
			return null;
		}
	}

}
