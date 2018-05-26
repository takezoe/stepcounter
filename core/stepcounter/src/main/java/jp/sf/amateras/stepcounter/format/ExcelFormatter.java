package jp.sf.amateras.stepcounter.format;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.sf.amateras.stepcounter.CategoryStepDto;
import jp.sf.amateras.stepcounter.CountResult;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * カウント結果をExcelで出力します。
 *
 * @author Naoki Takezoe
 */
public class ExcelFormatter implements ResultFormatter {

	public byte[] format(CountResult[] result) {
		try {
			InputStream in = ExcelFormatter.class.getResourceAsStream("ExcelFormatter.xls");

			List<CategoryStepDto> categories = new ArrayList<CategoryStepDto>();
			CategoryStepDto nonCategory = new CategoryStepDto();
			nonCategory.setCategory("");
			boolean useNonCategory = false;
			for (CountResult resultDto : result) {
				CategoryStepDto categoryDto = null;
				if (resultDto.getCategory() == null || "".equals(resultDto.getCategory())) {
					categoryDto = nonCategory;
					useNonCategory = true;
				} else {
					categoryDto = getCategoryDto(categories, resultDto.getCategory());
				}
				categoryDto.setStep(categoryDto.getStep() + resultDto.getStep());
				categoryDto.setNone(categoryDto.getNone() + resultDto.getNon());
				categoryDto.setComment(categoryDto.getComment() + resultDto.getComment());
			}
			if (useNonCategory) {
				categories.add(nonCategory);
			}

			Collections.sort(categories, new Comparator<CategoryStepDto>() {
				public int compare(CategoryStepDto o1, CategoryStepDto o2) {
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

			// カテゴリ・ファイルタイプが無指定の場合はnullから空文字に修正する。(fishplate対応)
			for (CountResult r : result) {
				if (r.getCategory() == null) {
					r.setCategory("");
				}
				if (r.getFileType() == null) {
					r.setFileType("未対応");
				}
			}

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("results", result);
			data.put("categories", categories);

			return merge(in, data);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * jXLSを使用してExcelファイルを生成します。
	 * 引数で与えたテンプレートの入力ストリームはこのメソッド内でクローズされます。
	 */
	private static byte[] merge(InputStream in, Map<String, Object> data) throws Exception {
		XLSTransformer transformer = new XLSTransformer();
		Workbook workbook = transformer.transformXLS(in, data);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);

		return out.toByteArray();
	}

	private static CategoryStepDto getCategoryDto(
			List<CategoryStepDto> categoryResult, String category) {
		for (CategoryStepDto categoryDto : categoryResult) {
			if (categoryDto.getCategory().equals(category)) {
				return categoryDto;
			}
		}

		CategoryStepDto categoryDto = new CategoryStepDto();
		categoryDto.setCategory(category);
		categoryResult.add(categoryDto);

		return categoryDto;
	}

}
