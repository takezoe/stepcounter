package jp.sf.amateras.stepcounter;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * テーブルの選択部分をクリップボードにコピーするためのリスナ
 */
public class TableCopyListener extends SelectionAdapter {

	private Table table;
	private Clipboard clipboard;

	public TableCopyListener(Table table, Clipboard clipboard){
		this.table = table;
		this.clipboard = clipboard;
	}

	public void widgetSelected(SelectionEvent e) {
		TableItem[] items = table.getSelection();
		if(items.length==0){
			return;
		}
		StringBuffer sb = new StringBuffer();
		int columnCount = table.getColumnCount();

		for(int i=0;i<items.length;i++){
			for(int j=0; j < columnCount; j++){
				sb.append(items[i].getText(j));
				if(j == columnCount - 1){
					sb.append("\n"); //$NON-NLS-1$
				} else {
					sb.append("\t"); //$NON-NLS-1$
				}
			}
		}
		TextTransfer transfer = TextTransfer.getInstance();
		clipboard.setContents(new Object[]{sb.toString()},new Transfer[]{transfer});
	}
}
