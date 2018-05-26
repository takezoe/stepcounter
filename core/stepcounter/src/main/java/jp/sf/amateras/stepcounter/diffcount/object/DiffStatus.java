package jp.sf.amateras.stepcounter.diffcount.object;

/**
 * ファイル、ディレクトリの変更状況を示す列挙型です。
 *
 * @author Naoki Takezoe
 */
public enum DiffStatus {

	/**
	 * 変更なしを示すステータスです。
	 */
	NONE {
		@Override
		public String toString() {
			return "変更なし";
		}
	},

	/**
	 * 追加を示すステータスです。
	 */
	ADDED {
		@Override
		public String toString() {
			return "新規";
		}
	},

	/**
	 * 変更を示すステータスです。
	 */
	MODIFIED {
		@Override
		public String toString() {
			return "変更";
		}
	},

	/**
	 * 削除を示すステータスです。
	 */
	REMOVED {
		@Override
		public String toString() {
			return "削除";
		}
	},

	/**
	 * サポート対象外を示すステータスです。
	 */
	UNSUPPORTED {
		@Override
		public String toString() {
			return "サポート対象外";
		}
	}
}
