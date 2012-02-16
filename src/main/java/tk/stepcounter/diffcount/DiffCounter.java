package tk.stepcounter.diffcount;

import java.io.File;

import tk.stepcounter.diffcount.diff.DiffEngine;
import tk.stepcounter.diffcount.diff.IDiffHandler;
import tk.stepcounter.diffcount.object.AbstractDiffResult;
import tk.stepcounter.diffcount.object.DiffFileResult;
import tk.stepcounter.diffcount.object.DiffFolderResult;
import tk.stepcounter.diffcount.object.DiffStatus;

/**
 * 差分のカウント処理を行います。
 * 
 */
public class DiffCounter {

	private static String	encoding	= null;

	public static void setEncoding(String encoding) {
		DiffCounter.encoding = encoding;
	}

	private static String getEncoding(File file) {
		if (encoding != null) {
			return encoding;
		}
		return DiffCounterUtil.getFileEncoding(file);
	}

	/**
	 * 2つのディレクトリ配下のソースコードの差分をカウントします。
	 *
	 * @param oldRoot 変更前のソースツリーのルートディレクトリ
	 * @param newRoot 変更後のソースツリーのルートディレクトリ
	 * @return カウント結果
	 */
	public static DiffFolderResult count(File oldRoot, File newRoot) {

		// TODO ファイル単位での差分にも対応する。

		DiffFolderResult root = new DiffFolderResult(null);
		root.setName(newRoot.getName());

		diffFolder(root, oldRoot, newRoot);

		return root;
	}

	private static void diffFolder(DiffFolderResult parent, File oldFolder,
			File newFolder) {
		File[] oldFiles = oldFolder.listFiles();
		if (oldFiles == null) {
			oldFiles = new File[0];
		}

		File[] newFiles = newFolder.listFiles();

		for (File newFile : newFiles) {
			if (DiffCounterUtil.isIgnore(newFile)) {
				continue;
			}

			boolean found = false;

			for (File oldFile : oldFiles) {
				if (newFile.getName().equals(oldFile.getName())) {
					AbstractDiffResult result = createDiffResult(parent,
							oldFile, newFile, getEncoding(newFile),
							DiffStatus.MODIFIED);
					if (result != null) {
						parent.addChild(result);
					}
					found = true;
					break;
				}
			}

			// 古いソースツリーに見つからなかった場合は追加
			if (found == false) {
				AbstractDiffResult result = createDiffResult(parent, null,
						newFile, getEncoding(newFile), DiffStatus.ADDED);
				parent.addChild(result);
			}

			// ディレクトリの場合は再帰的に処理
			if (newFile.isDirectory()) {
				DiffFolderResult newParent = (DiffFolderResult)parent.getChildren().get(
						parent.getChildren().size() - 1);
				diffFolder(newParent, new File(oldFolder, newFile.getName()),
						newFile);
			} else {

			}
		}

		// 削除されたフォルダを抽出。二回まわすのは非効率ですが…
		for (File oldFile : oldFiles) {
			if (DiffCounterUtil.isIgnore(oldFile)) {
				continue;
			}

			boolean found = false;

			for (File newFile : newFiles) {
				if (oldFile.getName().equals(newFile.getName())) {
					found = true;
					break;
				}
			}

			if (found == false) {
				parent.addChild(createDiffResult(parent, oldFile, null,
						getEncoding(oldFile), DiffStatus.REMOVED));
			}
		}

	}

	private static AbstractDiffResult createDiffResult(DiffFolderResult parent,
			File oldFile, File newFile, String charset, DiffStatus status) {

		if (newFile != null && newFile.isFile()) {
			DiffFileResult diffResult;

			Cutter cutter = CutterFactory.getCutter(newFile);
			if (cutter != null) {
				diffResult = createDiffFileResult(parent, oldFile, newFile,
						charset, status, cutter);
			} else {
				// カッターが取得できなかった場合はサポート対象外とする
				diffResult = new DiffFileResult(parent);
				diffResult.setName(newFile.getName());
				diffResult.setStatus(DiffStatus.UNSUPPORTED);
				diffResult.setAddCount(0);
				diffResult.setFileType(CutterFactory.getFileType(newFile));
			}

			return diffResult;

		} else if (oldFile != null && oldFile.isFile()) {
			DiffFileResult diffResult = new DiffFileResult(parent);
			diffResult.setName(oldFile.getName());
			diffResult.setStatus(status);
			diffResult.setFileType(CutterFactory.getFileType(oldFile));

			Cutter cutter = CutterFactory.getCutter(oldFile);

			if (cutter != null && status == DiffStatus.REMOVED) {
				// 削除ファイルの場合
				DiffSource source = cutter.cut(DiffCounterUtil.getSource(
						oldFile, charset));
				if (source.isIgnore()) {
					return null;
				}
				diffResult.setDelCount(DiffCounterUtil.split(source.getSource()).length);
				diffResult.setCategory(source.getCategory());
			}

			return diffResult;

		} else if (newFile != null && newFile.isDirectory()) {
			DiffFolderResult diffResult = new DiffFolderResult(parent);
			diffResult.setName(newFile.getName());
			diffResult.setStatus(status);
			return diffResult;

		} else if (oldFile != null && oldFile.isDirectory()) {
			DiffFolderResult diffResult = new DiffFolderResult(parent);
			diffResult.setName(oldFile.getName());
			diffResult.setStatus(status);
			return diffResult;
		}

		return null;
	}

	private static DiffFileResult createDiffFileResult(DiffFolderResult parent,
			File oldFile, File newFile, String charset, DiffStatus status,
			Cutter cutter) {

		DiffFileResult diffResult = new DiffFileResult(parent);

		diffResult.setFileType(CutterFactory.getFileType(newFile));
		diffResult.setName(newFile.getName());
		diffResult.setStatus(status);

		if (status == DiffStatus.ADDED) {
			// 新規ファイルの場合
			DiffSource source = cutter.cut(DiffCounterUtil.getSource(newFile,
					charset));
			if (source.isIgnore()) {
				return null;
			}
			diffResult.setAddCount(DiffCounterUtil.split(source.getSource()).length);
			diffResult.setCategory(source.getCategory());
		} else if (status == DiffStatus.MODIFIED) {
			// 変更ファイルの場合
			DiffSource oldSource = cutter.cut(DiffCounterUtil.getSource(
					oldFile, charset));
			DiffSource newSource = cutter.cut(DiffCounterUtil.getSource(
					newFile, charset));

			if (newSource.isIgnore()) {
				return null;
			}

			DiffCountHandler handler = new DiffCountHandler();
			DiffEngine engine = new DiffEngine(handler, oldSource.getSource(),
					newSource.getSource());
			engine.doDiff();

			diffResult.setAddCount(handler.getAddCount());
			diffResult.setDelCount(handler.getDelCount());
			diffResult.setCategory(newSource.getCategory());

			if (handler.getAddCount() == 0) {
				diffResult.setStatus(DiffStatus.NONE);
			}
		} else if (status == DiffStatus.REMOVED) {
			// 削除ファイルの場合
			DiffSource source = cutter.cut(DiffCounterUtil.getSource(oldFile,
					charset));
			if (source.isIgnore()) {
				return null;
			}
			diffResult.setDelCount(DiffCounterUtil.split(source.getSource()).length);
			diffResult.setCategory(source.getCategory());
		} else {
			diffResult = null;
		}

		return diffResult;
	}

	/**
	 * 変更行数をカウントするための{@link IDiffHandler}実装クラスです。
	 */
	private static class DiffCountHandler implements IDiffHandler {

		/** 追加行数 */
		private int	addCount	= 0;

		/** 削除行数 */
		private int	delCount	= 0;

		/**
		 * {@inheritDoc}
		 */
		public void add(String text) {
			this.addCount++;
		}

		/**
		 * {@inheritDoc}
		 */
		public void delete(String text) {
			this.delCount++;
		}

		/**
		 * {@inheritDoc}
		 */
		public void match(String text) {}

		/**
		 * 追加行数を取得します。
		 * 
		 * @return 追加行数
		 */
		public int getAddCount() {
			return this.addCount;
		}

		/**
		 * 削除行数を取得します。
		 * 
		 * @return 削除行数
		 */
		public int getDelCount() {
			return this.delCount;
		}
	}

}
