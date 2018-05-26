package jp.sf.amateras.stepcounter.diffcount.renderer;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jp.sf.amateras.stepcounter.diffcount.DiffCounterUtil;
import jp.sf.amateras.stepcounter.diffcount.object.DiffFolderResult;
import jp.sf.amateras.stepcounter.format.ExcelFormatter;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.poi.ss.usermodel.Workbook;

public class ExcelRenderer implements Renderer {

	public byte[] render(DiffFolderResult root) {
		try {
			InputStream in = ExcelRenderer.class.getResourceAsStream("DiffExcelFormat.xls");

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("results", DiffCounterUtil.convertToList(root));
			data.put("totalAdd", root.getAddCount());
			data.put("totalDel", root.getDelCount());

			return merge(in, data);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * jXLSを使用してExcelファイルを生成します。
	 * 引数で与えたテンプレートの入力ストリームはこのメソッド内でクローズされます。
	 * <p>
	 * TODO {@link ExcelFormatter}と共通化する
	 */
	private static byte[] merge(InputStream in, Map<String, Object> data) throws Exception {
		XLSTransformer transformer = new XLSTransformer();
		Workbook workbook = transformer.transformXLS(in, data);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);

		return out.toByteArray();
	}

}
