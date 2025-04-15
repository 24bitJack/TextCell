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
		if (!spr.checkForRecursionInLoc(super.fullCellText())) {
			return 0.0;
		} else {
		return calcSingle(super.abbreviatedCellText());
		}
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
		// TODO Auto-generated method stub
		return super.abbreviatedCellText();
	}
	
	private String average(String beg, String end,boolean isSum) {
		System.out.println("Average begins: ");
		double avg = 0.0;
		System.out.println("beginning: " + beg + " End: " + end);
		if (beg.equalsIgnoreCase(end)) {
			return spr.ProcessCell("",end.toUpperCase());
		}
		SpreadsheetLocation locationBeg = new SpreadsheetLocation(beg);
		SpreadsheetLocation locationEnd = new SpreadsheetLocation(end);
		int RowDif = locationEnd.getRow() - locationBeg.getRow();
		int ColDif = locationEnd.getCol() - locationBeg.getCol();
		int TotalCellsInBetween = ColDif*20 + RowDif;
		int dividen = 1;
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
					System.out.println(formulaCell.getDoubleVal());
					avg += formulaCell.getDoubleVal();
					dividen++;
				} else if (spr.getCell(loc) instanceof RealCell Rcell) {
					avg += Rcell.getDoubleVal();
					dividen++;
				}
			}
			
		} else if (RowDif == 0) {
			SpreadsheetLocation loc;
			for (int i = locationBeg.getCol(); i <= locationEnd.getCol();i++) {
				String currentLocation = Character.toString((char) i + 'A') + "" + (locationBeg.getRow()+1);
				System.out.println(currentLocation);
				loc = new SpreadsheetLocation(currentLocation);
				if (spr.getCell(loc) instanceof FormulaCell formulaCell) {
					System.out.println(formulaCell.getDoubleVal());
					avg += formulaCell.getDoubleVal();
					dividen++;
				} else if (spr.getCell(loc) instanceof RealCell Rcell) {
					avg += Rcell.getDoubleVal();
					dividen++;
				}
			}
		} else {
			SpreadsheetLocation loc;
			int i;
			for (i = locationBeg.getCol()*20 + locationBeg.getRow(); i <= locationBeg.getCol()*20 + locationBeg.getRow()+ TotalCellsInBetween;i++) {
				char Column = (char) ('A' + ((int) Math.floor(i/20)));
				if ((int) Math.floor(i/20) <= locationEnd.getCol() || (!((int) Math.floor(i/20) == locationEnd.getCol()) && i%20+1 > locationEnd.getCol())) { 
				String CellLocation = Column + "" + (1+i%20);
				System.out.println("Current Cell location: " + CellLocation + " Current 'i' value: " + dividen + " current SUM : " + avg);
				loc = new SpreadsheetLocation((CellLocation));
				System.out.println("processed value: " + spr.getCell(loc).fullCellText());
				if (spr.getCell(loc) instanceof FormulaCell formulaCell) {
					avg += formulaCell.getDoubleVal();
					dividen++;
				} else if (spr.getCell(loc) instanceof RealCell Rcell) {
					System.out.println(Rcell.getDoubleVal());
					avg += Rcell.getDoubleVal();
					System.out.println("currentAVG: " + avg);
					dividen++;
				} else {
					System.out.println("not a realCell " + CellLocation);
				}
			}
			}
		}
		if (isSum) {
			System.out.println("sum: " + avg);
			return Double.toString(avg);
		} else {
			if (TotalCellsInBetween == 0) {
				TotalCellsInBetween = 1;
			}
			System.out.println("avg: " + avg/(dividen-1));
			return Double.toString(avg/(dividen-1));
		}
		
	}

	public String Sumative(String beg, String end) {
		return Double.toString(Double.parseDouble(average(beg,end,true)));
	}
	
	public double calcSingle(String str) {
		System.out.println("Calculations begin: " + str);
		str = str.replace("(","").replace(")","").replace(" ", "");
		if (str.toUpperCase().trim().startsWith("AVG")) {
			str = str.trim().toUpperCase();
			return Double.parseDouble(average(str.replace("AVG","").split("-")[0],str.split("-")[1],false));
		}
		if (str.toUpperCase().trim().startsWith("SUM")) {
			str = str.trim().toUpperCase();
			return Double.parseDouble(Sumative(str.replace("SUM","").split("-")[0],str.split("-")[1]));
		} 
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
		System.out.println(Components.toString());
		char Opperand;
		for (int i = 0; i < Components.size(); i++) {
			CurrentToken = Components.get(i);
			if (spr.isCell(CurrentToken) || spr.containsCell(CurrentToken)) {
			   SpreadsheetLocation loc = (new SpreadsheetLocation(CurrentToken.replace("-","")));
			   
			   if (spr.getCell(loc) instanceof FormulaCell formulaCell) {
				   if (!spr.checkForRecursionInLoc(loc)) {
					   System.out.println("ValueCell called " + formulaCell.getDoubleVal());
					   CurrentToken = CurrentToken.contains("-") ? "-" + Double.toString(formulaCell.getDoubleVal()) :Double.toString(formulaCell.getDoubleVal());
					   Components.set(i, CurrentToken);
				   } else {
					   Components.set(i, "0.0");
				   }

			   } else if (spr.getCell(loc) instanceof RealCell valueCell) {
				   System.out.println("ValueCell called " + valueCell.getDoubleVal());
				   CurrentToken = CurrentToken.contains("-") ? "-" + Double.toString(valueCell.getDoubleVal()):Double.toString(valueCell.getDoubleVal());
				   Components.set(i, CurrentToken);
			   } else {
				   System.out.println("Empty/TextCell called ");
				   CurrentToken = ""; 
				   Components.set(i, CurrentToken);
			   }
			}
			 System.out.println(CurrentToken);
		}
		System.out.println(Components.toString());
		for (int i = 0; i < Components.size()-1;i++) {
			CurrentToken = Components.get(i);
			System.out.println("current Token :" + CurrentToken);
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
				System.out.println("currentToken : " + currentToken);
				if(currentToken.matches("-?\\d+(\\.\\d+)?")) {
				ReturnValue += Double.parseDouble(currentToken);
				}
			}
		}
		System.out.println("returns :" + ReturnValue);
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
