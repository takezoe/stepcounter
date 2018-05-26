package jp.sf.amateras.stepcounter.diffcount;

import java.util.Comparator;

import jp.sf.amateras.stepcounter.diffcount.object.AbstractDiffResult;
import jp.sf.amateras.stepcounter.diffcount.object.DiffFolderResult;


public class FileComparator implements Comparator<AbstractDiffResult> {

	public int compare(AbstractDiffResult o1, AbstractDiffResult o2) {
		// 両方ともディレクトリの場合
		if (o1 instanceof DiffFolderResult && o2 instanceof DiffFolderResult) {
			return o1.getName().compareTo(o2.getName());
		}
		// 片方のみディレクトリ場合
		if (o1 instanceof DiffFolderResult) {
			return -1;
		} else if (o2 instanceof DiffFolderResult) {
			return 1;
		}
		// 両方ともファイルの場合
		return o1.getName().compareTo(o2.getName());
	}

}
