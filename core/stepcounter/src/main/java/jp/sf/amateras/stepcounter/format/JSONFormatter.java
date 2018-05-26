package jp.sf.amateras.stepcounter.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import jp.sf.amateras.stepcounter.CountResult;

/**
 * カウント結果をJSON形式でフォーマットします。
 */
public class JSONFormatter implements ResultFormatter {
	private static final int[] ESCAPE_CHARS = new int[128];
	
	static {
		for (int i = 0; i < 32; i++) {
			ESCAPE_CHARS[i] = -1;
		}
		ESCAPE_CHARS['\b'] = 'b';
		ESCAPE_CHARS['\t'] = 't';
		ESCAPE_CHARS['\n'] = 'n';
		ESCAPE_CHARS['\f'] = 'f';
		ESCAPE_CHARS['\r'] = 'r';
		ESCAPE_CHARS['"'] = '"';
		ESCAPE_CHARS['\\'] = '\\';
		ESCAPE_CHARS['<'] = -2;
		ESCAPE_CHARS['>'] = -2;
		ESCAPE_CHARS[0x7F] = -1;
	}

	public byte[] format(CountResult[] results) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Writer writer = new OutputStreamWriter(out, "UTF-8");
			writer.append("[");
			
			boolean first = true;
			for (CountResult result : results) {
				if (first) {
					writer.append("\n");
				} else {
					writer.append(",\n");
				}
				
				writer.append("\t{ ");
				writer.append("\"name\": \"");
				escape(writer, result.getFileName());
				writer.append("\", ");
				
				// 未対応の形式をフォーマット
				if (result.getFileType() == null || result.getFileType().length() == 0) {
					writer.append("\"type\": \"unknown\"");
				// 正常にカウントされたものをフォーマット
				} else {
					writer.append("\"type\": \"");
					escape(writer, result.getFileType());
					writer.append("\", ");
					if (result.getCategory() != null && result.getCategory().length() > 0) {
						writer.append("\"category\": \"");
						escape(writer, result.getCategory());
						writer.append("\", ");
					}
					
					long total = result.getStep() + result.getNon() + result.getComment();
					writer.append("\"step\": ").append(Long.toString(result.getStep())).append(", ");
					writer.append("\"none\": ").append(Long.toString(result.getNon())).append(", ");
					writer.append("\"comment\": ").append(Long.toString(result.getComment())).append(", ");
					writer.append("\"total\": ").append(Long.toString(total)).append(" ");
				}
				writer.append("}");
				
				first = false;
			}			
			
			writer.write("\n]\n");
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
				int x = ESCAPE_CHARS[c];
				if (x == 0) {
					writer.append(c);
				} else if (x > 0) {
					writer.append('\\');
					writer.append((char)x);
				} else if (x == -1) {
					writer.append("\\u00");
					writer.append("0123456789ABCDEF".charAt(c / 16));
					writer.append("0123456789ABCDEF".charAt(c % 16));
				}
			} else {
				writer.append(c);
			}
		}
	}
}

