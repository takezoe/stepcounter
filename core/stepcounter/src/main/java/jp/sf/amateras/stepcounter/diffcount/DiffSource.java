package jp.sf.amateras.stepcounter.diffcount;

public class DiffSource {

	private String source;
	private String category;
	private boolean isIgnore;

	public DiffSource(String source, boolean isIgnore, String category){
		this.source = source;
		this.isIgnore = isIgnore;
		this.category = category;

		if(this.category == null){
			this.category = "";
		}
	}

	public String getSource() {
		return source;
	}

	public boolean isIgnore(){
		return isIgnore;
	}

	public String getCategory() {
		return category;
	}

}
