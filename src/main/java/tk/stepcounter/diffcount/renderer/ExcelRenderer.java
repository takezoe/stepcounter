package tk.stepcounter.diffcount.renderer;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.seasar.fisshplate.template.FPTemplate;

import tk.stepcounter.Util;
import tk.stepcounter.diffcount.DiffCounterUtil;
import tk.stepcounter.diffcount.object.DiffFolderResult;
import tk.stepcounter.format.ExcelFormatter;

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
	 * Fisshplateを使用してExcelファイルを生成します。
	 * 引数で与えたテンプレートの入力ストリームはこのメソッド内でクローズされます。
	 * <p>
	 * TODO {@link ExcelFormatter}と共通化する
	 */
	private static byte[] merge(InputStream in, Map<String, Object> data)
			throws Exception {
		FPTemplate template = new FPTemplate();
		HSSFWorkbook wb;

		try {
			wb = template.process(in, data);
		} catch (Exception ex) {
			throw ex;
		} finally {
			Util.close(in);
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		wb.write(out);

		return out.toByteArray();
	}

}
