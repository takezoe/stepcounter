package jp.sf.amateras.stepcounter;

import java.util.List;

/**
 * 差分カウント時のカテゴリ別の集計用DTOです。
 *
 * @author takanori
 *
 */
public class CategoryDiffDto extends CategoryDto {

	/** 追加行数 */
	private long	addCount	= 0;

	/** 削除行数 */
	private long	delCount	= 0;

	/**
	 * デフォルトコンストラクタ。
	 */
	public CategoryDiffDto() {}

	public long getAddCount() {
		return addCount;
	}

	public void setAddCount(long addCount) {
		this.addCount = addCount;
	}

	public long getDelCount() {
		return delCount;
	}

	public void setDelCount(long delCount) {
		this.delCount = delCount;
	}

	/**
	 * カテゴリDTOのリストから、指定されたカテゴリのDTOを取得します。
	 *
	 * @param categoryList カテゴリDTOのリスト
	 * @param category カテゴリ
	 * @return カテゴリDTO
	 */
	public static CategoryDiffDto getDto(List<CategoryDiffDto> categoryList,
			String category) {
		for (CategoryDiffDto dto : categoryList) {
			if (dto.getCategory().equals(category)) {
				return dto;
			}
		}

		CategoryDiffDto dto = new CategoryDiffDto();
		dto.setCategory(category);
		categoryList.add(dto);

		return dto;
	}
}
