package jp.sf.amateras.stepcounter;

import java.util.List;

/**
 * ステップカウント時のカテゴリ別の集計用DTOです。
 *
 *  * @author takanori
 *
 */
public class CategoryStepDto extends CategoryDto {

	/** 実行行数 */
	private long	step;

	/** コメント行数 */
	private long	comment;

	/** 空行数 */
	private long	none;

	/**
	 * デフォルトコンストラクタ。
	 */
	public CategoryStepDto() {}

	public long getStep() {
		return step;
	}

	public void setStep(long step) {
		this.step = step;
	}

	public long getComment() {
		return comment;
	}

	public void setComment(long comment) {
		this.comment = comment;
	}

	public long getNone() {
		return none;
	}

	public void setNone(long none) {
		this.none = none;
	}

	/**
	 * カテゴリDTOのリストから、指定されたカテゴリのDTOを取得します。
	 *
	 * @param categoryList カテゴリDTOのリスト
	 * @param category カテゴリ
	 * @return カテゴリDTO
	 */
	public static CategoryStepDto getDto(List<CategoryStepDto> categoryList,
			String category) {
		for (CategoryStepDto dto : categoryList) {
			if (dto.getCategory().equals(category)) {
				return dto;
			}
		}

		CategoryStepDto dto = new CategoryStepDto();
		dto.setCategory(category);
		categoryList.add(dto);

		return dto;
	}
}
