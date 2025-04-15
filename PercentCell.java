package textExcel;

public class PercentCell extends RealCell{
	
	public PercentCell(String percent) {
		super(percent);
		
	}
	public String abbreviatedCellText() {
		String text = super.abbreviatedCellText();
		if (text.trim().startsWith("0.") ||text.trim().startsWith("-0.")) {
		return text.length() > 10? text.substring(0,9) + "%" : text.substring(0,9) + "%" ;
		} else {
			text = Double.toString(Double.parseDouble(super.abbreviatedCellText().replace("%","")));
			text = text.substring(0,text.indexOf("."));
			return text.length() > 9? text.substring(0,9) + "%" : text + "%" + " ".repeat(9 - text.length());
		}
	}
	public double getDoubleVal() {
		return Double.parseDouble((super.abbreviatedCellText()).replace("%", ""))/100.0;
	}

	public String fullCellText() {
		return Double.toString(Double.parseDouble(super.abbreviatedCellText().replace("%", ""))/100);
	}

}
