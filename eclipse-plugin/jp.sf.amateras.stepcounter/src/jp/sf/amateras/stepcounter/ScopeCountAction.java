package jp.sf.amateras.stepcounter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * 「範囲範囲をカウント」メニュー
 */
public class ScopeCountAction implements IObjectActionDelegate {

	private ISelection selection = null;

	private static final String TEMP_FILE = "stepcounter_scopeCountTempFile";

	/**
	 * コンストラクタ
	 */
	public ScopeCountAction() {
		super();
	}

	/* (非 Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		// エディタ上の選択範囲の情報を取得する
		IEditorPart editor = window.getActivePage().getActiveEditor();

		if (!(editor instanceof ITextEditor)) {
			// サポート外のエディタで実行された場合、「未対応エディタ」と結果に出力する
			try {
				window.getActivePage().showView("jp.sf.amateras.stepcounter.ScopeCountView");
			} catch (PartInitException e) {
				e.printStackTrace();
			}
			IViewReference[] views = window.getActivePage().getViewReferences();
			for(int i=0;i<views.length;i++){
				IViewPart view = views[i].getView(false);
				if(view instanceof ScopeCountView){
					((ScopeCountView)view).notSupported(selection);
				}
			}
		}
		else {

			ITextEditor textEditor = (ITextEditor)editor;
			ITextSelection textSelection = (ITextSelection)textEditor.getSelectionProvider().getSelection();

			IDocumentProvider documentProvider = textEditor.getDocumentProvider();
			IDocument document = documentProvider.getDocument(textEditor.getEditorInput());

			// 選択範囲の開始行
			int startLine = textSelection.getStartLine();
			// 選択範囲の終了行
			int endLine   = textSelection.getEndLine();

			// カウント対象が存在するプロジェクトを取得
			IFileEditorInput editorInput = (IFileEditorInput)textEditor.getEditorInput();
			IFile file = editorInput.getFile();
			IProject project = file.getProject();

			// 選択範囲の行頭から行末までを含むデータでファイルを作成し、カウント処理に渡す
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IPath path = project.getFullPath().append(TEMP_FILE);
			IFile scopeFile  = root.getFile(path);
			try {
				StringWriter sw = new StringWriter();
				for(int line=startLine; line <= endLine; line++){
					int length = document.getLineLength(line);
					int offset = document.getLineOffset(line);
					sw.write(document.get(offset, length));
				}

				InputStream in = new ByteArrayInputStream(sw.toString().getBytes());
				if (scopeFile.exists()) {
					// 重複した場合、元ファイルを誰が作ったかは不明なので、維持しておく
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSSS");
					String addDate = sdf.format(new Date(System.currentTimeMillis()));
					IPath datePath = path.removeLastSegments(1);
					path = datePath.append(TEMP_FILE.concat(addDate));
					scopeFile = root.getFile(path);
				}

				// 新規作成
				scopeFile.create(in, true, null);

				in.close();

				processToUseScope(window, scopeFile);

			} catch(Exception ex){
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
			finally {
				// 作成した一時ファイルを削除する
				try {
					if (scopeFile != null) {
						scopeFile.delete(false, null);
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 選択範囲の情報を用いた処理を実行する。<br>
	 * ここでは「選択範囲のカウント結果」ビューに、選択範囲内のステップ数カウント結果を表示する。
	 *
	 * @param window 現在アクティブなWorkbenchWindow
	 * @param scopeFile 選択範囲のデータを持つファイル
	 */
	protected void processToUseScope(IWorkbenchWindow window, IFile scopeFile) {

		try {
			window.getActivePage().showView("jp.sf.amateras.stepcounter.ScopeCountView");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		IViewReference[] views = window.getActivePage().getViewReferences();
		for(int i=0;i<views.length;i++){
			IViewPart view = views[i].getView(false);
			if(view instanceof ScopeCountView){
				((ScopeCountView)view).count(selection, scopeFile);
			}
		}
	}

	/* (非 Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	/* (非 Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

}
