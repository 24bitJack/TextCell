package textExcel;

public class PercentCell extends ValueCell{
	
	public PercentCell(String percent) {
		super(percent);
		
	}
	public String abbreviatedCellText() {
		String str = OgT.substring(0,OgT.indexOf("."));
		return str.length() < 9 ? str + "%"  + " ".repeat(9-str.length()) :str.substring(0,9) + "%";
	}
	public double getDoubleVal() {
		return Double.parseDouble(OgT.replace("%", ""));
	}

	public String fullCellText() {
		return Double.toString(Double.parseDouble(this.OgT.replace("%",""))/100);
	}

}
