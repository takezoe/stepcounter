package jp.sf.amateras.stepcounter;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;

/**
 * テーブルの全ての行を選択するためのリスナ。
 */
public class TableSelectAllListener extends SelectionAdapter {

	private Table table;

	public TableSelectAllListener(Table table) {
		this.table = table;
	}

	public void widgetSelected(SelectionEvent e) {
		this.table.selectAll();
	}
}
