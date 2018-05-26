package jp.sf.amateras.stepcounter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * カテゴリ別集計用のDTOです。
 *
 * @author takanori
 *
 */
public abstract class CategoryDto {

	/** カテゴリ */
	private String	category;

	/**
	 * デフォルトコンストラクタ。
	 */
	public CategoryDto() {}

	/**
	 * カテゴリを取得します。
	 *
	 * @return カテゴリ
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * カテゴリを設定します。
	 *
	 * @param category カテゴリ
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * ソートします。
	 *
	 * @param categoryList カテゴリDTOのリスト
	 */
	public static void sort(List<? extends CategoryDto> categoryList) {
		Collections.sort(categoryList, new Comparator<CategoryDto>() {
			public int compare(CategoryDto o1, CategoryDto o2) {
				if (o1.getCategory().length() == 0
						&& o2.getCategory().length() == 0) {
					return 0;
				}
				if (o1.getCategory().length() == 0) {
					return 1;
				}
				if (o2.getCategory().length() == 0) {
					return -1;
				}
				return o1.getCategory().compareTo(o2.getCategory());
			}
		});
	}
}
