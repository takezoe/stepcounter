package jp.sf.amateras.stepcounter.diffcount.renderer;

public class RendererFactory {

	public static Renderer getRenderer(String name) {
		if(name != null){
			if (name.equals("text")) {
				return new SimpleRenderer();

			} else if (name.equals("html")) {
				return new HTMLRenderer();

			} else if (name.equals("excel")) {
				return new ExcelRenderer();
			}
		}
		return new SimpleRenderer();
	}

}
