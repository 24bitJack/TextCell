package textExcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormulaCell extends RealCell{
	
	private Spreadsheet spr;
	
	public FormulaCell(String Ortxt,Spreadsheet spr) {
		super(Ortxt);
		System.out.println("FormulaCellEstablished");
		this.spr = spr;
	}
	
	public String getVal() {
		return super.abbreviatedCellText();
	}
	
	public double getDoubleVal() {
		return calcSingle(super.abbreviatedCellText());
	}
	
	@Override
	public String abbreviatedCellText() {
		double anss = (getDoubleVal());
		if (Double.isNaN(anss) || Double.isInfinite(anss )) {
			return "#ERROR    ";
		} else {
			String ans = Double.toString(anss);
		return ans.length() > 10 ? ans.substring(0,10) 
								 : ans + " ".repeat(10 - ans.length());
		}
	}
	
	@Override
	public String fullCellText() {
		return super.abbreviatedCellText();
	}
	
	private String average(String beg, String end,boolean isSum) {
		if (beg.trim().equalsIgnoreCase(end.trim())) {
			if (spr.getCell(new SpreadsheetLocation(beg)) instanceof RealCell) {
				return Double.toString(((RealCell) spr.getCell(new SpreadsheetLocation(beg))).getDoubleVal());
			}
		}
		SpreadsheetLocation[] locationsOfCalcCells = spr.IsolateCell("SUM" + beg +"-" +  end);
		int dividen = 0;
		double returnValue = 0;
		
		for (SpreadsheetLocation CellLocs : locationsOfCalcCells) {
					if (spr.getCell(CellLocs) instanceof FormulaCell fcell) {
						System.out.println("Average -> FormulaCell value: " + fcell.getDoubleVal());
						returnValue += fcell.getDoubleVal();
						dividen++;
						System.out.println("average -> returnValue: " + returnValue);
					} else if (spr.getCell(CellLocs) instanceof RealCell rcell) {
						System.out.println("Average -> realCell value: " + rcell.getDoubleVal());
						returnValue += rcell.getDoubleVal();
						dividen++;
					}
					System.out.println("average -> returnValue: " + returnValue);
			}
		System.out.println("average -> returnValue: " + returnValue);
		return isSum ? Double.toString(returnValue) : Double.toString(returnValue/dividen);
		}
	
	
	
	public String Sumative(String beg, String end) {
		return Double.toString(Double.parseDouble(average(beg,end,true)));
	}
	
	public SpreadsheetLocation[] allValuesInbetweenSUM(String cellText) {
		cellText = cellText.toUpperCase().replace("SUM", "").replace("AVG","");
		String[] begToEnd = cellText.split("-");
		String beg = begToEnd[0];
		String end = begToEnd[1];
		ArrayList<SpreadsheetLocation> Location = new ArrayList<SpreadsheetLocation>();
		SpreadsheetLocation locationBeg = new SpreadsheetLocation(beg);
		
		if (beg.equalsIgnoreCase(end)) {
			Location.add(locationBeg);
			return (SpreadsheetLocation[]) Location.toArray();
		}
		
		SpreadsheetLocation locationEnd = new SpreadsheetLocation(end);
		int RowDif = locationEnd.getRow() - locationBeg.getRow();
		int ColDif = locationEnd.getCol() - locationBeg.getCol();
		int TotalCellsInBetween = ColDif*20 + RowDif;
		System.out.println("total cells in between: " + TotalCellsInBetween);
		if (ColDif == 0) {
			SpreadsheetLocation loc;
			char Column = beg.charAt(0);
			for (int i = locationBeg.getRow()+1; i <= locationBeg.getRow()+1+RowDif;i++) {
				String CellLocation = Column + "" + i;
				System.out.println("Current Cell location: " + CellLocation);
				loc = new SpreadsheetLocation((CellLocation));
				System.out.println("processed value: " + spr.getCell(loc).fullCellText());
				if (spr.getCell(loc) instanceof FormulaCell formulaCell) {
					Location.add(loc);

				} else if (spr.getCell(loc) instanceof RealCell Rcell) {
					Location.add(loc);
				}
			}
			
		} else if (RowDif == 0) {
			SpreadsheetLocation loc;
			for (int i = locationBeg.getCol(); i <= locationEnd.getCol();i++) {
				String currentLocation = Character.toString((char) i + 'A') + "" + (locationBeg.getRow()+1);
				System.out.println(currentLocation);
				loc = new SpreadsheetLocation(currentLocation);
				if (spr.getCell(loc) instanceof FormulaCell formulaCell) {
					Location.add(loc);
				} else if (spr.getCell(loc) instanceof RealCell Rcell) {
					Location.add(loc);
				}
			}
		} else {
			SpreadsheetLocation loc;
			int i;
			for (i = locationBeg.getCol()*20 + locationBeg.getRow(); i <= locationBeg.getCol()*20 + locationBeg.getRow()+ TotalCellsInBetween;i++) {
				char Column = (char) ('A' + ((int) Math.floor(i/20)));
				if ((int) Math.floor(i/20) <= locationEnd.getCol() || (!((int) Math.floor(i/20) == locationEnd.getCol()) && i%20+1 > locationEnd.getCol())) { 
				String CellLocation = Column + "" + (1+i%20);
				loc = new SpreadsheetLocation((CellLocation));
				System.out.println("processed value: " + spr.getCell(loc).fullCellText());
				if (spr.getCell(loc) instanceof FormulaCell formulaCell) {
					Location.add(loc);
				} else if (spr.getCell(loc) instanceof RealCell Rcell) {
					System.out.println(Rcell.getDoubleVal());
					Location.add(loc);
				} else {
					System.out.println("not a realCell " + CellLocation);
				}
			}
			}
		}
		return (SpreadsheetLocation[]) Location.toArray();
	}
	
	
	public double calcSingle(String str) {
		System.out.println("calcSingle -> Calculations begin: " + str);
		str = str.replace("(","").replace(")","").replace(" ", "");
		if (str.toUpperCase().trim().startsWith("AVG")) {
			str = str.trim().toUpperCase();
			return Double.parseDouble(average(str.replace("AVG","").split("-")[0],str.split("-")[1],false));
		}
		if (str.toUpperCase().trim().startsWith("SUM")) {
			str = str.trim().toUpperCase();
			return Double.parseDouble(Sumative(str.replace("SUM","").split("-")[0],str.split("-")[1]));
		} 
		String UnsortedCellText = "";
		if (spr.isCell(str.trim())) {
			SpreadsheetLocation loc = new SpreadsheetLocation(str.trim());
			if (spr.getCell(loc) instanceof FormulaCell Fcell) {
				return Fcell.getDoubleVal();
			} else if (spr.getCell(loc) instanceof RealCell rcell) {
				return  rcell.getDoubleVal();
			} else  if (spr.getCell(loc) instanceof EmptyCell || spr.getCell(loc) instanceof TextCell ){
				return Double.NaN;
			}
		}
		
		ArrayList<String> Components = new ArrayList<String>();
		Components = spr.addAll(spr.SplitStringPlusClean(str));
		double ReturnValue = 0.0
				,CurrentDouble = Double.NaN;
		String CurrentToken;
		System.out.println("calcSingle ->" + Components.toString());
		char Opperand;
		for (int i = 0; i < Components.size(); i++) {
			CurrentToken = Components.get(i);
			if (spr.isCell(CurrentToken) || spr.containsCell(CurrentToken)) {
			   SpreadsheetLocation loc = (new SpreadsheetLocation(CurrentToken.replace("-","")));
			   
			   if (spr.getCell(loc) instanceof FormulaCell formulaCell) {
					   System.out.println("calcSingle -> FormulaCell called " + formulaCell.getDoubleVal());
					   CurrentToken = CurrentToken.contains("-") ? "-" + Double.toString(formulaCell.getDoubleVal()) :Double.toString(formulaCell.getDoubleVal());
					   Components.set(i, CurrentToken);

			   } else if (spr.getCell(loc) instanceof RealCell valueCell) {
				   System.out.println("calcSingle -> ValueCell called " + valueCell.getDoubleVal());
				   CurrentToken = CurrentToken.contains("-") ? "-" + Double.toString(valueCell.getDoubleVal()):Double.toString(valueCell.getDoubleVal());
				   Components.set(i, CurrentToken);
			   } else {
				   System.out.println("calcSingle -> Empty/TextCell called ");
				   CurrentToken = ""; 
				   Components.set(i, CurrentToken);
			   }
			}
			 System.out.println(CurrentToken);
		}
		System.out.println(Components.toString());
		for (int i = 0; i < Components.size()-1;i++) {
			CurrentToken = Components.get(i);
			System.out.println("calcSingle -> current Token :" + CurrentToken);
			if (!CurrentToken.isBlank()) {
				if (CurrentToken.contains("*")) { //[a /^* b] --> [c]
					Components.set(i,Double.toString(doubleSimpleCalculation(Components.get(i-1),Components.get(i+1) , '*')));
					Components.remove(i+1);
					Components.remove(i-1);
					i--;
				} else if (CurrentToken.contains("/")) { //[a /^* b] --> [c]
					double CurrentValue = doubleSimpleCalculation(Components.get(i-1),Components.get(i+1), '/');
					if (Double.isInfinite(CurrentValue)) {
						return CurrentValue;
					}
					Components.set(i,Double.toString(CurrentValue));
					Components.remove(i+1);
					Components.remove(i-1);
					i--;
				}
			} else {
				Components.remove(i);
				i--;
			}
		}
		for (String currentToken: Components) {
			if (!currentToken.isBlank())  {
				System.out.println("calcSingle -> currentToken : " + currentToken);
				if(currentToken.matches("-?\\d+(\\.\\d+)?")) {
				ReturnValue += Double.parseDouble(currentToken);
				}
			}
		}
		System.out.println("calcSingle -> returns :" + ReturnValue);
		return ReturnValue;
		}
	
	public double doubleSimpleCalculation(String Ex1, String Ex2, char Opperand) {
		SpreadsheetLocation loc;
		if (spr.isCell(Ex1.replace("-", "")) || spr.containsCell(Ex1)) {
			loc = new SpreadsheetLocation(Ex1.replace("-", ""));
			Ex1 = Ex1.contains("-") ?"-" + (spr.ProcessCell("",loc)) : (spr.ProcessCell("",loc));
		}
		if (spr.isCell(Ex2.replace("-", "")) || spr.containsCell(Ex2)) {
			loc = new SpreadsheetLocation(Ex2.replace("-", ""));
			Ex2 = Ex2.contains("-") ?"-" + (spr.ProcessCell("",loc)) : (spr.ProcessCell("",loc));
		}
		switch (Opperand) {
		case '/':
			if (Ex2.equals("0") || Ex2.equals("0.0")) {
				return Ex1.contains("-") ? Double.POSITIVE_INFINITY : Double.POSITIVE_INFINITY;
			}
			return Double.parseDouble(Ex1.replaceAll("[^-0-9.]", "")) / Double.parseDouble(Ex2.replaceAll("[^-0-9.]", ""));
		case '*':
			return Double.parseDouble(Ex1.replaceAll("[^-0-9.]", "")) * Double.parseDouble(Ex2.replaceAll("[^-0-9.]", "")); 
		case '+':
			return Double.parseDouble(Ex1.replaceAll("[^-0-9.]", "")) + Double.parseDouble(Ex2.replaceAll("[^-0-9.]", "")); 
			default:
				return 0.0;
		}
	}
	
}
