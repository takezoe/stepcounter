package jp.sf.amateras.stepcounter.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import jp.sf.amateras.stepcounter.CountResult;

/**
 * カウント結果をXML形式でフォーマットします。
 */
public class XMLFormatter implements ResultFormatter {
	private static final char[] ESCAPE_CHARS = new char[128];
	
	static {
		for (int i = 0; i < 32; i++) {
			ESCAPE_CHARS[i] = 1;
		}
		ESCAPE_CHARS['<'] = 2;
		ESCAPE_CHARS['>'] = 3;
		ESCAPE_CHARS['&'] = 4;
		ESCAPE_CHARS['"'] = 5;
		ESCAPE_CHARS[0x7F] = 1;
	}

	public byte[] format(CountResult[] results) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Writer writer = new OutputStreamWriter(out, "UTF-8");
			writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			writer.append("<stepcounter>\n");
			
			for (CountResult result : results) {
				writer.append("\t<file ");
				writer.append("name=\"");
				escape(writer, result.getFileName());
				writer.append("\" ");
				
				// 未対応の形式をフォーマット
				if (result.getFileType() == null || result.getFileType().length() == 0) {
					writer.append("type=\"unknown\" ");
				// 正常にカウントされたものをフォーマット
				} else {
					writer.append("type=\"");
					escape(writer, result.getFileType());
					writer.append("\" ");
					if (result.getCategory() != null && result.getCategory().length() > 0) {
						writer.append("category=\"");
						escape(writer, result.getCategory());
						writer.append("\" ");
					}

					long total = result.getStep() + result.getNon() + result.getComment();
					writer.append("step=\"").append(Long.toString(result.getStep())).append("\" ");
					writer.append("none=\"").append(Long.toString(result.getNon())).append("\" ");
					writer.append("comment=\"").append(Long.toString(result.getComment())).append("\" ");
					writer.append("total=\"").append(Long.toString(total)).append("\" ");
				}
				writer.append("/>\n");
			}			
			
			writer.write("</stepcounter>\n");
			writer.flush();
		} catch (IOException e) {
			// not happen
			e.printStackTrace();
		}
		
		return out.toByteArray();
	}
	
	private static void escape(Writer writer, String text) throws IOException {
		if (text == null) return;
		
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c < ESCAPE_CHARS.length) {
				switch (ESCAPE_CHARS[c]) {
				case 1:
					writer.append("&#").append(Integer.toString(c)).append(";");
					break;
				case 2:
					writer.append("&lt;");
					break;
				case 3:
					writer.append("&gt;");
					break;
				case 4:
					writer.append("&amp;");
					break;
				case 5:
					writer.append("&quot;");
					break;
				default:
					writer.append(c);
				}
			} else {
				writer.append(c);
			}
		}
	}
}
