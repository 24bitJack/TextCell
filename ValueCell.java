package textExcel;

public class ValueCell extends RealCell{
	public ValueCell(String Ogtxt) {
		super(Ogtxt);
	}
	@Override
	public String abbreviatedCellText() {//Kaden taught me how to use ?:
		 String str = Double.toString(Double.parseDouble(super.abbreviatedCellText()));
		 return str.length() > 10 
	            ?str.substring(0, 10) 
	            : str + " ".repeat(10 - str.length());
	}
	
	public String fullCellText() {
		 return super.abbreviatedCellText();
	}
	public double getDoubleVal() {
		return Double.parseDouble(super.abbreviatedCellText());
	}
}
