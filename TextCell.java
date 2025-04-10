package textExcel;

public class TextCell implements Cell{
	private String text;
	
	public TextCell(String text) {
		this.text = text;
	}
	@Override
	public String abbreviatedCellText() {
		String str = text.replace("\"", "");
		if (str.length() <= 10) {
				str += " ".repeat(10 - str.length());
			}
		else {
				str = str.substring(0,10);
		}
		return str;
	} 
	@Override
	public String fullCellText() {
		return text;
	}
	
}
