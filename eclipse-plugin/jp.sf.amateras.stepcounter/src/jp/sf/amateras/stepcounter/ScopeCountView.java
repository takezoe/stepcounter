package jp.sf.amateras.stepcounter;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jp.sf.amateras.stepcounter.format.ExcelFormatter;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

/**
 * 選択範囲のカウント結果を表示するためのViewPart。<br>
 * StepCountViewの動作を踏襲するため、StepCounterPluginResources.propertiesは、
 * StepCountViewと同じ値を利用する。
 *
 * @see ViewPart
 */
public class ScopeCountView extends ViewPart {

	private TabFolder				tabFolder;
	private Table					fileTable;
	private Table					categoryTable;
	private Menu					filePopup;
	private Menu					categoryPopup;
	private MenuItem				copy1;
	private MenuItem				copy2;
	private MenuItem				saveExcel1;
	private MenuItem				saveExcel2;
	private MenuItem				selectAll1;
	private MenuItem				selectAll2;
	private MenuItem				clear1;
	private MenuItem				clear2;
	private MenuItem				open;
	private Clipboard				clipboard;

	private static final String		FILE		= StepCounterPlugin.getResourceString("StepCountView.columnName");		//$NON-NLS-1$
	private static final String		TYPE		= StepCounterPlugin.getResourceString("StepCountView.columnType");		//$NON-NLS-1$
	private static final String		CATEGORY	= StepCounterPlugin.getResourceString("StepCountView.columnCategory");	//$NON-NLS-1$
	private static final String		STEP		= StepCounterPlugin.getResourceString("StepCountView.columnStep");		//$NON-NLS-1$
	private static final String		NONE		= StepCounterPlugin.getResourceString("StepCountView.columnNone");		//$NON-NLS-1$
	private static final String		COMMENT		= StepCounterPlugin.getResourceString("StepCountView.columnComment");	//$NON-NLS-1$
	private static final String		TOTAL		= StepCounterPlugin.getResourceString("StepCountView.columnTotal");		//$NON-NLS-1$

	private HashMap<String, IFile>	files		= new HashMap<String, IFile>();

	private List<CountResult>		results		= new ArrayList<CountResult>();

	/**
	 * コンストラクタ
	 */
	public ScopeCountView() {
		super();
	}

	/**
	 * ViewPartの中身を作成。
	 *
	 * @see ViewPart#createPartControl
	 */
	public void createPartControl(Composite parent) {
		// タブを作成
		tabFolder = new TabFolder(parent, SWT.NULL);

		TabItem tabItem1 = new TabItem(tabFolder, SWT.NULL);
		tabItem1.setText(StepCounterPlugin.getResourceString("StepCountView.tabFile"));

		Composite composite1 = new Composite(tabFolder, SWT.NULL);
		composite1.setLayout(new FillLayout());
		tabItem1.setControl(composite1);

		// ファイル別のテーブルを作成
		fileTable = new Table(composite1, SWT.FULL_SELECTION | SWT.MULTI);
		fileTable.setHeaderVisible(true);
		fileTable.setLinesVisible(true);
		String[] cols1 = { FILE, TYPE, CATEGORY, STEP, NONE, COMMENT, TOTAL };
		for (int i = 0; i < cols1.length; i++) {
			TableColumn col = null;
			if (i == 0 || i == 1 || i == 2) {
				col = new TableColumn(fileTable, SWT.LEFT);
			} else {
				col = new TableColumn(fileTable, SWT.RIGHT);
			}
			col.setText(cols1[i]);
			if (i == 0) {
				col.setWidth(250);
			} else {
				col.setWidth(80);
			}
			col.addSelectionListener(new FileTableHeaderListener());
		}

		// カテゴリ別のテーブルを作成
		TabItem tabItem2 = new TabItem(tabFolder, SWT.NULL);
		tabItem2.setText(StepCounterPlugin.getResourceString("StepCountView.tabCategory"));

		Composite composite2 = new Composite(tabFolder, SWT.NULL);
		composite2.setLayout(new FillLayout());
		tabItem2.setControl(composite2);

		categoryTable = new Table(composite2, SWT.FULL_SELECTION | SWT.MULTI);
		categoryTable.setHeaderVisible(true);
		categoryTable.setLinesVisible(true);
		String[] cols2 = { CATEGORY, STEP, NONE, COMMENT, TOTAL };
		for (int i = 0; i < cols2.length; i++) {
			TableColumn col = null;
			if (i == 0) {
				col = new TableColumn(categoryTable, SWT.LEFT);
			} else {
				col = new TableColumn(categoryTable, SWT.RIGHT);
			}
			col.setText(cols2[i]);
			if (i == 0) {
				col.setWidth(250);
			} else {
				col.setWidth(80);
			}
			col.addSelectionListener(new CategoryTableHeaderListener());
		}

		// クリップボードの準備
		clipboard = new Clipboard(parent.getDisplay());

		// テーブルにポップアップメニューを追加
		{
			filePopup = new Menu(fileTable.getShell(), SWT.POP_UP);

			open = new MenuItem(filePopup, SWT.PUSH);
			open.setText(StepCounterPlugin.getResourceString("StepCountView.menuOpen")); //$NON-NLS-1$
			open.addSelectionListener(new TableOpenListener());

			copy1 = new MenuItem(filePopup, SWT.PUSH);
			copy1.setText(StepCounterPlugin.getResourceString("StepCountView.menuCopy")); //$NON-NLS-1$
			copy1.addSelectionListener(new TableCopyListener(fileTable,
					clipboard));

			saveExcel1 = new MenuItem(filePopup, SWT.PUSH);
			saveExcel1.setText(StepCounterPlugin.getResourceString("StepCountView.menuExcel")); //$NON-NLS-1$
			saveExcel1.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					saveToExcel();
				}
			});

			selectAll1 = new MenuItem(filePopup, SWT.PUSH);
			selectAll1.setText(StepCounterPlugin.getResourceString("StepCountView.menuSelectAll")); //$NON-NLS-1$
			selectAll1.addSelectionListener(new TableSelectAllListener(
					fileTable));

			new MenuItem(filePopup, SWT.SEPARATOR);

			clear1 = new MenuItem(filePopup, SWT.PUSH);
			clear1.setText(StepCounterPlugin.getResourceString("StepCountView.menuClear")); //$NON-NLS-1$
			clear1.addSelectionListener(new TableClearListener());

			fileTable.addMouseListener(new TableMouseListener1());
		}
		{
			categoryPopup = new Menu(categoryTable.getShell(), SWT.POP_UP);

			copy2 = new MenuItem(categoryPopup, SWT.PUSH);
			copy2.setText(StepCounterPlugin.getResourceString("StepCountView.menuCopy")); //$NON-NLS-1$
			copy2.addSelectionListener(new TableCopyListener(categoryTable,
					clipboard));

			saveExcel2 = new MenuItem(categoryPopup, SWT.PUSH);
			saveExcel2.setText(StepCounterPlugin.getResourceString("StepCountView.menuExcel")); //$NON-NLS-1$
			saveExcel2.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					saveToExcel();
				}
			});

			selectAll2 = new MenuItem(categoryPopup, SWT.PUSH);
			selectAll2.setText(StepCounterPlugin.getResourceString("StepCountView.menuSelectAll")); //$NON-NLS-1$
			selectAll2.addSelectionListener(new TableSelectAllListener(
					categoryTable));

			new MenuItem(categoryPopup, SWT.SEPARATOR);

			clear2 = new MenuItem(categoryPopup, SWT.PUSH);
			clear2.setText(StepCounterPlugin.getResourceString("StepCountView.menuClear")); //$NON-NLS-1$
			clear2.addSelectionListener(new TableClearListener());

			categoryTable.addMouseListener(new TableMouseListener2());
		}
	}

	/**
	 * Excelに保存する。
	 */
	private void saveToExcel() {
		// エクスポート先のファイルを指定
		FileDialog dialog = new FileDialog(
				Display.getDefault().getActiveShell(), SWT.SAVE);
		dialog.setFilterExtensions(new String[] { "*.xls" });
		String path = dialog.open();
		if (path != null) {
			ExcelFormatter formatter = new ExcelFormatter();
			byte[] data = formatter.format(results.toArray(new CountResult[results.size()]));
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(path);
				out.write(data);
			} catch (Exception ex) {
				throw new RuntimeException("An error occurred while creating the Excel file.", ex);
			} finally {
				Util.close(out);
			}
		}
	}

	/**
	 * カウントを実行
	 *
	 * @param selection IStructuredSelection
	 * @param scopeFile カウント範囲のデータのみを持つファイル
	 */
	public void count(ISelection selection, IFile scopeFile) {
		// すべて削除
		fileTable.removeAll();
		files.clear();
		results.clear();
		categoryTable.removeAll();

		if (selection != null && selection instanceof IStructuredSelection) {

			IStructuredSelection iSel = (IStructuredSelection)selection;

			@SuppressWarnings("unchecked")
			Iterator<Object> ite = iSel.iterator();

			long totalStep = 0;
			long totalComment = 0;
			long totalNone = 0;
			List<CategoryStepDto> categoryResult = new ArrayList<CategoryStepDto>();

			while (ite.hasNext()) {
				Object obj = ite.next();
				CountResult result = null;
				if (obj instanceof IFile) {
					// 選択範囲のカウントを実行
					result = countScope((IFile)obj, scopeFile, categoryResult);
				}
				if (result != null) {
					totalStep += result.getStep();
					totalNone += result.getNon();
					totalComment += result.getComment();
				}
			}
			{
				// ファイル単位の合計行を表示
				String[] data = {
						TOTAL,
						"", //$NON-NLS-1$
						"", //$NON-NLS-1$
						String.valueOf(totalStep), String.valueOf(totalNone),
						String.valueOf(totalComment),
						String.valueOf(totalStep + totalNone + totalComment) };
				TableItem item = new TableItem(fileTable, SWT.NULL);
				item.setText(data);
			}

			// カテゴリ別の集計値を表示
			totalStep = 0;
			totalNone = 0;
			totalComment = 0;

			// カテゴリをソート
			CategoryDto.sort(categoryResult);

			for (CategoryStepDto categoryDto : categoryResult) {
				String[] categoryData = {
						categoryDto.getCategory(),
						String.valueOf(categoryDto.getStep()),
						String.valueOf(categoryDto.getNone()),
						String.valueOf(categoryDto.getComment()),
						String.valueOf(categoryDto.getStep()
								+ categoryDto.getNone()
								+ categoryDto.getComment()), };

				TableItem categoryItem = new TableItem(categoryTable, SWT.NULL);
				categoryItem.setText(categoryData);

				totalStep += categoryDto.getStep();
				totalNone += categoryDto.getNone();
				totalComment += categoryDto.getComment();
			}
			{
				// カテゴリ単位の合計行を表示
				String[] data = { TOTAL, String.valueOf(totalStep),
						String.valueOf(totalNone),
						String.valueOf(totalComment),
						String.valueOf(totalStep + totalNone + totalComment) };
				TableItem item = new TableItem(categoryTable, SWT.NULL);
				item.setText(data);
			}
		}
	}

	/**
	 * 選択範囲をカウント
	 *
	 * @param file 選択範囲が存在している元ファイル
	 * @param scopeFile 選択範囲で構成されたファイル
	 * @param categoryResult カテゴリ情報
	 * @return 選択範囲内のカウント結果
	 */
	private CountResult countScope(
			IFile file,
			IFile scopeFile,
			List<CategoryStepDto> categoryResult) {

		if(files.containsValue(file)){
			return null;
		}
		try {
			StepCounter counter = StepCounterFactory.getCounter(file.getName());
			if (counter != null) {

				// 対応するカウンタが存在する場合
				CountResult result = counter.count(
						scopeFile.getLocation().makeAbsolute().toFile(),
						scopeFile.getCharset());
				if (result == null) {
					// カウント対象外
					return null;
				}

				results.add(result);

				String type = result.getFileType();
				String category = result.getCategory();
				long comment = result.getComment();
				long none = result.getNon();
				long step = result.getStep();

				CategoryStepDto categoryDto = CategoryStepDto.getDto(categoryResult, result.getCategory());
				categoryDto.setStep(categoryDto.getStep() + result.getStep());
				categoryDto.setNone(categoryDto.getNone() + result.getNon());
				categoryDto.setComment(categoryDto.getComment() + result.getComment());

				String[] data = {
						file.getFullPath().toString(),
						//FILE.getName(),
						type, category, String.valueOf(step),
						String.valueOf(none), String.valueOf(comment),
						String.valueOf(step + none + comment) };
				TableItem item = new TableItem(fileTable, SWT.NULL);
				item.setText(data);
				files.put(file.getFullPath().toString(), file);

				return result;

			} else {
				// 対応するカウンタが存在しない場合
				String[] data = {
						file.getFullPath().toString(),
						//FILE.getName(),
						StepCounterPlugin.getResourceString("StepCountView.notSupported"), //$NON-NLS-1$
						"", "", //$NON-NLS-1$
						"", //$NON-NLS-1$
						"", //$NON-NLS-1$
						"" //$NON-NLS-1$
				};
				TableItem item = new TableItem(fileTable, SWT.NULL);
				item.setText(data);
				files.put(file.getFullPath().toString(), file);
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(file.getName() + "でエラーが発生しました！");
			return null;
		}
	}

	/**
	 * サポート外のエディタに対するカウント動作。
	 *
	 * @param selection IStructuredSelection
	 */
	public void notSupported(ISelection selection) {

		// すべて削除
		fileTable.removeAll();
		files.clear();
		results.clear();
		categoryTable.removeAll();

		if (selection != null && selection instanceof IStructuredSelection) {

			IStructuredSelection iSel = (IStructuredSelection)selection;

			@SuppressWarnings("unchecked")
			Iterator<Object> ite = iSel.iterator();

			long totalStep = 0;
			long totalComment = 0;
			long totalNone = 0;

			while (ite.hasNext()) {
				Object obj = ite.next();
				if (obj instanceof IFile) {
					IFile file = (IFile) obj;

					// エディタが対応していない
					String[] data = {
							file.getFullPath().toString(),
							//FILE.getName(),
							StepCounterPlugin.getResourceString("ScopeCountView.notSupported"), //$NON-NLS-1$
							"", "", //$NON-NLS-1$
							"", //$NON-NLS-1$
							"", //$NON-NLS-1$
							"" //$NON-NLS-1$
					};
					TableItem item = new TableItem(fileTable, SWT.NULL);
					item.setText(data);
					files.put(file.getFullPath().toString(), file);
				}
			}
			{
				// ファイル単位の合計行を表示
				String[] data = {
						TOTAL,
						"", //$NON-NLS-1$
						"", //$NON-NLS-1$
						String.valueOf(totalStep), String.valueOf(totalNone),
						String.valueOf(totalComment),
						String.valueOf(totalStep + totalNone + totalComment) };
				TableItem item = new TableItem(fileTable, SWT.NULL);
				item.setText(data);
			}
			{
				// カテゴリ単位の合計行を表示
				String[] data = { TOTAL, String.valueOf(totalStep),
						String.valueOf(totalNone),
						String.valueOf(totalComment),
						String.valueOf(totalStep + totalNone + totalComment) };
				TableItem item = new TableItem(categoryTable, SWT.NULL);
				item.setText(data);
			}
		}
	}

	/**
	 * @see ViewPart#setFocus
	 */
	public void setFocus() {
		fileTable.setFocus();
	}

	/**
	 * ファイル別テーブルのポップアップメニューの状態を更新します。
	 */
	private void updatePopupMenu1() {
		// 項目が１つ以上あれば「全て選択」「クリア」「Excelファイルに保存」を活性化
		TableItem[] items = fileTable.getItems();
		if (items.length == 0) {
			selectAll1.setEnabled(false);
			clear1.setEnabled(false);
			saveExcel1.setEnabled(false);
		} else {
			selectAll1.setEnabled(true);
			clear1.setEnabled(true);
			saveExcel1.setEnabled(true);
		}
		// 項目が１つでも選択されていれば「コピー」を活性化
		TableItem[] selection = fileTable.getSelection();
		if (selection.length == 0) {
			copy1.setEnabled(false);
		} else {
			copy1.setEnabled(true);
		}
		// ファイルが１つでも選択されていれば「開く」を活性化
		open.setEnabled(false);
		for (int i = 0; i < selection.length; i++) {
			String filePath = selection[i].getText(0);
			if (files.get(filePath) != null) {
				open.setEnabled(true);
				break;
			}
		}
	}

	/**
	 * カテゴリ別テーブルのポップアップメニューの状態を更新します。
	 */
	private void updatePopupMenu2() {
		// 項目が１つ以上あれば「全て選択」「クリア」「Excelファイルに保存」を活性化
		TableItem[] items = categoryTable.getItems();
		if (items.length == 0) {
			selectAll2.setEnabled(false);
			clear2.setEnabled(false);
			saveExcel2.setEnabled(false);
		} else {
			selectAll2.setEnabled(true);
			clear2.setEnabled(true);
			saveExcel2.setEnabled(true);
		}
		// 項目が１つでも選択されていれば「コピー」を活性化
		TableItem[] selection = categoryTable.getSelection();
		if (selection.length == 0) {
			copy2.setEnabled(false);
		} else {
			copy2.setEnabled(true);
		}
	}

	/**
	 * テーブルで選択状態になっているファイルをエディタで開きます。
	 */
	private void openEditor() {
		TableItem[] items = fileTable.getSelection();
		for (int i = 0; i < items.length; i++) {
			try {
				String filePath = items[i].getText(0);
				if (files.get(filePath) != null) {
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					// Eclipse 3.0対応
					IDE.openEditor(window.getActivePage(),
							(IFile)files.get(filePath), true);
				}
			} catch (Exception ex) {
				// TODO 例外は握りつぶしておきます…
			}
		}
	}

	/**
	 * ファイル単位のテーブルのヘッダがクリックされた際にソートを行うリスナ
	 */
	private class FileTableHeaderListener extends SelectionAdapter {

		private int	sortColumn	= 0;

		private int	sortOrder	= TableComparator.ASC;

		public void widgetSelected(SelectionEvent e) {
			try {
				// ソートするカラムを決定
				TableColumn column = (TableColumn)e.getSource();
				int selectColumn = 0;
				String name = column.getText();
				if (name.equals(FILE)) {
					selectColumn = 0;
				} else if (name.equals(TYPE)) {
					selectColumn = 1;
				} else if (name.equals(CATEGORY)) {
					selectColumn = 2;
				} else if (name.equals(STEP)) {
					selectColumn = 3;
				} else if (name.equals(NONE)) {
					selectColumn = 4;
				} else if (name.equals(COMMENT)) {
					selectColumn = 5;
				} else if (name.equals(TOTAL)) {
					selectColumn = 6;
				}

				if (this.sortColumn != selectColumn) {
					this.sortOrder = TableComparator.ASC;
				}
				this.sortColumn = selectColumn;

				// データをいったんArrayListに格納
				TableItem[] items = fileTable.getItems();
				ArrayList<String[]> list = new ArrayList<String[]>();
				for (int i = 0; i < items.length; i++) {
					list.add(new String[] { items[i].getText(0),
							items[i].getText(1), items[i].getText(2),
							items[i].getText(3), items[i].getText(4),
							items[i].getText(5), items[i].getText(6) });
				}

				// ソートする
				String[][] datas = list.toArray(new String[list.size()][]);
				Arrays.sort(datas, new TableComparator(sortColumn, 3,
						TableComparator.ASC));
				this.sortOrder = this.sortOrder * -1;

				// データを再表示
				fileTable.removeAll();
				for (int i = 0; i < datas.length; i++) {
					TableItem item = new TableItem(fileTable, SWT.NULL);
					item.setText(datas[i]);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * カテゴリ単位のテーブルのヘッダがクリックされた際にソートを行うリスナ
	 */
	private class CategoryTableHeaderListener extends SelectionAdapter {

		private int	sortColumn	= 0;

		private int	sortOrder	= TableComparator.ASC;

		public void widgetSelected(SelectionEvent e) {
			try {
				// ソートするカラムを決定
				TableColumn column = (TableColumn)e.getSource();
				int selectColumn = 0;
				String name = column.getText();
				if (name.equals(CATEGORY)) {
					selectColumn = 0;
				} else if (name.equals(STEP)) {
					selectColumn = 1;
				} else if (name.equals(NONE)) {
					selectColumn = 2;
				} else if (name.equals(COMMENT)) {
					selectColumn = 3;
				} else if (name.equals(TOTAL)) {
					selectColumn = 4;
				}

				if (this.sortColumn != selectColumn) {
					this.sortOrder = TableComparator.ASC;
				}
				this.sortColumn = selectColumn;

				// データをいったんArrayListに格納
				TableItem[] items = categoryTable.getItems();
				ArrayList<String[]> list = new ArrayList<String[]>();
				for (int i = 0; i < items.length; i++) {
					list.add(new String[] { items[i].getText(0),
							items[i].getText(1), items[i].getText(2),
							items[i].getText(3), items[i].getText(4) });
				}

				// ソートする
				String[][] datas = list.toArray(new String[list.size()][]);
				Arrays.sort(datas, new TableComparator(sortColumn, 1,
						TableComparator.ASC));
				this.sortOrder = this.sortOrder * -1;

				// データを再表示
				categoryTable.removeAll();
				for (int i = 0; i < datas.length; i++) {
					TableItem item = new TableItem(categoryTable, SWT.NULL);
					item.setText(datas[i]);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/** ファイル別テーブルのポップアップメニューを表示するためのマウスリスナ */
	private class TableMouseListener1 extends MouseAdapter {
		public void mouseUp(MouseEvent e) {
			if (e.button == 3) {
				updatePopupMenu1();
				filePopup.setVisible(true);
			}
		}

		public void mouseDoubleClick(MouseEvent e) {
			openEditor();
		}
	}

	/** カテゴリ別テーブルのポップアップメニューを表示するためのマウスリスナ */
	private class TableMouseListener2 extends MouseAdapter {
		public void mouseUp(MouseEvent e) {
			if (e.button == 3) {
				updatePopupMenu2();
				categoryPopup.setVisible(true);
			}
		}
	}

	/** テーブルで選択されたファイルを開くためのリスナ */
	private class TableOpenListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			openEditor();
		}
	}

	/** テーブルの表示内容をクリアするためのリスナ */
	private class TableClearListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			fileTable.removeAll();
			categoryTable.removeAll();
			files.clear();
			results.clear();
		}
	}
}
