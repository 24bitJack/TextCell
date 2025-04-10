package textExcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormulaCell extends RealCell{
	private double ans;
	public FormulaCell(String Ortxt) {
		super(Ortxt);
	}
	
	public void setAnswer(Double val) {
		ans = val;
	}
	public String getVal() {
		return OgT;
	}
	
	
	@Override
	public String abbreviatedCellText() {
		String anss = Double.toString(ans);
		return anss.length() > 10 ? anss.substring(0,10) 
								 : anss + " ".repeat(10 - anss.length());
	}
	
	@Override
	public String fullCellText() {
		// TODO Auto-generated method stub
		return this.OgT;
	}
}
