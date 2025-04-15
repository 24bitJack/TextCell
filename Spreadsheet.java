package textExcel;

import java.util.ArrayList;
import java.util.Arrays;

// Update this file with your own code.

public class Spreadsheet implements Grid
{
	public SpreadsheetLocation currentFormulaLocation;
	public final EmptyCell empty = new EmptyCell();
	private Cell[][] sheet;
	public Spreadsheet() {
		currentFormulaLocation = null;
		this.sheet = new Cell[getCols()][getRows()];
		clearSheet();
	}
	
	@Override
	public String processCommand(String command) {//outsource all labor from this to external methods
		Location loc;
		if (command.toLowerCase().startsWith("clear")) { //clearing of Cell
			if (command.equalsIgnoreCase("clear")) {
				clearSheet();
				return getGridText();
			} else {
				loc = new SpreadsheetLocation(command.replaceAll("\\s+", " ").trim().split(" ")[1]);
				clearCell(loc);
				return getGridText();
			}
		} if (isCell(command.trim().split(" ")[0])) {
	    	if (isCell(command)) {
	    		return  ObtainCell(command);
	    	} else {
	    		ProcessCell(command ,"");	
	    		return getGridText();
	    	}
	    }
	    
	    return "";
	}
	
	public String ObtainCell(String str) {
		SpreadsheetLocation CellLoc = new SpreadsheetLocation(str.trim());
		return getCell(CellLoc).fullCellText();
	}
	
	public SpreadsheetLocation[] IsolateCell(String str) {
			ArrayList<SpreadsheetLocation> CellArray = new ArrayList<>();
			ArrayList<SpreadsheetLocation> Location = new ArrayList<SpreadsheetLocation>();
			SpreadsheetLocation CurrentLocation;
			if (str.toUpperCase().contains("SORTA")||str.toUpperCase().contains("SUM")||str.toUpperCase().contains("AVG")) {
				str = str.toUpperCase().replace("SORTA", "").replace("SUM", "").replace("AVG", "");
				String[] begToEnd = str.split("-");
				String beg = begToEnd[0].trim();
				String end = begToEnd[1].trim();
				SpreadsheetLocation locationBeg = new SpreadsheetLocation(beg);
				SpreadsheetLocation locationEnd = new SpreadsheetLocation(end);
				if (beg.equals(end)) {
					CellArray.add(locationBeg);
					return (SpreadsheetLocation[]) CellArray.toArray();
				}
				int RowDif = locationEnd.getRow() - locationBeg.getRow();
				int ColDif = locationEnd.getCol() - locationBeg.getCol();
				for (int i = locationBeg.getCol(); i <= locationBeg.getCol() + ColDif; i++) {
					char column = (char) ( Math.floor(i) + 'A');
					for (int j = locationBeg.getRow()+1; j <= locationBeg.getRow()+1 + RowDif; j++) {
						CurrentLocation = new SpreadsheetLocation(column + "" + j);
						System.out.println("method: IsolateCell -> CurrentLocation: " + column + "" + j);
						if (getCell(CurrentLocation) instanceof RealCell || getCell(CurrentLocation) instanceof FormulaCell ||getCell(CurrentLocation) instanceof TextCell) {
							CellArray.add(CurrentLocation);
						}
					}
				}
				SpreadsheetLocation[] returnArray =  new SpreadsheetLocation[CellArray.size()];
				System.out.print("IsolateCells -> CellArray");
				for (int i = 0; i < CellArray.size(); i++)  {
					System.out.print((char)(CellArray.get(i).getCol() +'A') +"" +CellArray.get(i).getRow()  + ", ");
					returnArray[i] = CellArray.get(i);
				}
				System.out.println();
				System.out.println("IsolateCells-> returnArray: " );
				return returnArray;
			} else {
				String[] Components = str.split("[-+/*]");
				for (String CurrentToken :Components) {
					if (isCell(CurrentToken.trim())) {
						CurrentLocation = new SpreadsheetLocation(CurrentToken);
						if (getCell(CurrentLocation) instanceof FormulaCell||getCell(CurrentLocation) instanceof RealCell||getCell(CurrentLocation) instanceof TextCell) {
							CellArray.add(CurrentLocation);
						}
					}
					
				}
				return (SpreadsheetLocation[]) CellArray.toArray();
			}

	}
	
	public String ProcessCell(String command, String CellLocStr) {
		Location loc;
		if(command.isBlank()) {
			 loc = new SpreadsheetLocation(CellLocStr);
			 Double retDouble;
			
			 if (getCell(loc) instanceof FormulaCell formulaCell) {
				 currentFormulaLocation = (SpreadsheetLocation) loc;
				 retDouble = (formulaCell.getDoubleVal());
				 if (Double.isNaN(retDouble)) {
					 return "#ERROR";
				 } else if (Double.isInfinite(retDouble)) {
					 return "#ERROR";
				 } else {
					 return Double.toString(retDouble);
				 }
			 } else if (getCell(loc) instanceof RealCell realcell) {
				 retDouble = (realcell.getDoubleVal());
				 if (Double.isNaN(retDouble)) {
					 return "#ERROR";
				 } else if (Double.isInfinite(retDouble)) {
					 return "#ERROR";
				 } else {
					 return Double.toString(retDouble);
				 }
			 } else {
				return getCell(loc).fullCellText();
			 }
		} else {
			String[] commandComponents = command.split("=",2);
			loc = new SpreadsheetLocation(commandComponents[0].trim());
			setCell(loc, commandComponents[1].trim());
			return getGridText();
		}
	}
	
	public String ProcessCell(String command, SpreadsheetLocation loc) {
		if(command.isBlank()) {
				if (getCell(loc) instanceof FormulaCell formulaCell) {
					return Double.toString(formulaCell.getDoubleVal());
				} else {
					return getCell(loc).fullCellText();
				}
		} else {
			String[] commandComponents = command.split("=",2);
			loc = new SpreadsheetLocation(commandComponents[0].trim());
			setCell(loc, commandComponents[1].trim());
			return getGridText();
		}
	}

	public void setCell(Location loc, String textToSet) {
		//textToSet == null -> emptyCell
		//sheet[loc.getCol()][loc.getRow()]
		Cell cell;
		if (textToSet.contains("\"")) {cell = new TextCell(textToSet);}
		else if (textToSet.contains(")") || textToSet.contains("+") || isCell(textToSet.toUpperCase())) {
			cell = new FormulaCell(textToSet,this);
			double ans = 0.0;
			currentFormulaLocation = (SpreadsheetLocation) loc;
			if (cell instanceof RealCell realcell) {
				 ans = realcell.getDoubleVal();
			} else if (cell instanceof FormulaCell formualaCell) {
				ans = formualaCell.getDoubleVal();
			}
			if (ans == Double.POSITIVE_INFINITY) {
				setCell(loc, "");
			}
			
			}
		else if(!textToSet.trim().replace("-","").matches("[^0-9]")&&textToSet.contains("%")) {cell = new PercentCell(textToSet);}
		else if (!textToSet.replace("-","").matches("[^0-9]")) {cell = new ValueCell(textToSet);}
		
		else {cell = new EmptyCell();}
		sheet[loc.getCol()][loc.getRow()] = cell;
	}
	
 	public void clearCell(Location loc) {
		sheet[loc.getCol()][loc.getRow()] = new EmptyCell();
	}
	
	public boolean containsCell(String str) {
		str = str.toUpperCase().replaceAll("[^A-Z0-9]", "").replace(" ", "").replace("[A-Z]", " $1").replace("[0-9]", "$1 ");
		String[] PosibleCells = str.split(" ");
		for(String i: PosibleCells) {
			if (!i.isBlank()) {
				if (isCell(i.replace(" ",""))) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isCell(String a) {
	    a = a.toUpperCase().trim();
	    if (a.length() < 2 || a.length() > 3) return false;
	    
	    char col = a.charAt(0);
	    String rowPart = a.substring(1);
	    
	    if (!Character.isLetter(col) || !rowPart.matches("\\d+")) return false;

	    int colNum = col - 'A';
	    int rowNum = Integer.parseInt(rowPart) - 1;

	    return colNum >= 0 && colNum < getCols() && rowNum >= 0 && rowNum < getRows();
	}
	
	public boolean isPercent(String str) {
		if (isValue(str.replace("%", " ")) && str.trim().endsWith("%")) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isFormula(String str) {
		if (str.trim().startsWith("(") && str.endsWith(")")) {
			return true;
		}
		return false;
	}
	
	public boolean isReal(String str) {
		str = str.trim();
		return str.matches("[^-+/*0-9A-Za-z.]");
	}
	
	public boolean isValue(String str) {
		str = str.trim();
		return str.matches("-?\\d+(\\.\\d+)?"); 
	}
	
	public boolean isDigit(char chr) {
		switch(chr) {
		case '0':
			return true;
		case '1':
			return true;
		case '2':
			return true;
		case '3':
			return true;
		case '4':
			return true;
		case '5':
			return true;
		case '6':
			return true;
		case '7':
			return true;
		case '8':
			return true;
		case '9':
			return true;
		default:
			return false;
			
		}
	}
	
	public void clearSheet() {
		sheet = new Cell[12][20];
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 20 ; j++) {
				sheet[i][j] = new EmptyCell();
			}
		}
	}
	
	public int getRows(){
		return 20;
	}

	@Override
	public int getCols()
	{
		return 12;
	}
	//
	private boolean isDigit(String string) {
		for (char c: string.toCharArray()) {
			if (!isDigit(c)) {
				return true;
			}
		}
		return false;
	}


	@Override
	public Cell getCell(Location loc) {
		return sheet[loc.getCol()][loc.getRow()];
	}


	@Override
	public String getGridText() {
	    String grid = "";
	    grid += "   ";
	    for (int j = 0; j < getCols(); j++) {grid += "|" + (char) (j + 65) + "         "; }
	    grid += "|\n";
	    for (int i = 0; i < getRows(); i++) {
	        grid += (i + 1); 
	        if ((i + 1) < 10) {
	            grid += "  ";
	        } else {
	        	grid += " ";
	        }
	        for (int j = 0; j < getCols(); j++) {
	            if (sheet[j][i] instanceof EmptyCell || sheet[j][i] == null) {
	                grid += "|          "; 
	            } else {
	            	if (isFormula(sheet[j][i].fullCellText())) {
	            		double val = sheet[j][i] instanceof RealCell ? (((RealCell) sheet[j][i]).getDoubleVal()) : (((FormulaCell) sheet[j][i]).getDoubleVal());
	            		grid += Double.toString(val).length() > 10 ? "|" +Double.toString(val).substring(0,10)
	            											:"|" + Double.toString(val) + " ".repeat(10 - Double.toString(val).length());
	            	} else {
	                grid += "|" + sheet[j][i].abbreviatedCellText();
	            	}
	            }
	        }
	        grid += "|\n"; 
	    }
	    return grid;
	}
	
	
	public ArrayList<String> addAll(String[] StringStr) {
		ArrayList<String> Components = new ArrayList<String>();
		for (String i: StringStr) {
			if (!i.isBlank()) {
				Components.add(i);
			}
		}
		return Components;
	}

	public String[] SplitStringPlusClean(String str) {
		if (str.contains("+") ||str.contains("-") ||str.contains("/") ||str.contains("*")) {
			str = str.replace("--","+")
					.replace("//","*")
					.replace("-","$-")
					.replace("+", "$")
					.replace("/", "$/$")
					.replace("*", "$*$")
					.replaceAll("[$]{2,}", "\\$");
			return str.split("\\$");
			
		} else {
			 String[] s = {str};
			return s;
		}
	}

	public String LocationToString(SpreadsheetLocation loc) {
		int row = loc.getRow();
		int col = loc.getCol();
		char colChar = (char) ( col +'A');
		return (colChar +"" + col);
	}
	
	
}