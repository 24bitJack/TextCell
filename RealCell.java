package textExcel;

public class RealCell implements Cell {
	protected String OgT;

	public RealCell(String Ogt) {
		this.OgT = Ogt;
	}
	@Override
	public String abbreviatedCellText() {
		// TODO Auto-generated method stub
		return OgT;
	}
	@Override
	public String fullCellText() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getDoubleVal() {
		return 0.0;
	}

}
