package textExcel;


public class EmptyCell implements Cell{
	public EmptyCell() {
	}
	@Override
	public String abbreviatedCellText() {
		// TODO Auto-generated method stub
		return "          ";
	}

	@Override
	public String fullCellText() {
		// TODO Auto-generated method stub
		return  "";
	}
}
