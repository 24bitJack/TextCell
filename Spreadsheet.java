package textExcel;

import java.util.ArrayList;
import java.util.Arrays;

// Update this file with your own code.

public class Spreadsheet implements Grid
{
	public ArrayList<SpreadsheetLocation> FormulaLocations;
	public SpreadsheetLocation currentFormulaLocation;
	public final EmptyCell empty = new EmptyCell();
	private Cell[][] sheet;
	public Spreadsheet() {
		FormulaLocations =  new ArrayList<SpreadsheetLocation>();
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
	    		return ProcessCell("" ,command);	
	    	} else {
	    		ProcessCell(command ,"");	
	    		return getGridText();
	    	}
	    }
	    
	    return "";
	}
	
	public boolean checkForRecursionInLoc(SpreadsheetLocation loc) {
	    if (FormulaLocations == null) {
	        FormulaLocations = new ArrayList<>();
	    }
	    
	    if (FormulaLocations.contains(loc)) {
	        return true; // Already visited
	    }

	    FormulaLocations.add(loc);
	    return false;
	}
	
	public void addLoctoFormulaLocation(SpreadsheetLocation loc) {
		System.out.println(this.LocationToString(loc));
		 if (FormulaLocations == null) {
			 System.out.println("FormulaLocation is Null");
			 FormulaLocations = new ArrayList<SpreadsheetLocation>();
			 currentFormulaLocation = loc;
			 System.out.println("formulalocation is no longer Null");
			 System.out.println("contains Formulallocation: " + currentFormulaLocation.toString());
			 FormulaLocations.add(currentFormulaLocation);
		 } else {
			 System.out.println("FormulaLocations In rotation: " + FormulaLocations.toString());
			 FormulaLocations.add(loc);
		 }
	}
	
	
	public boolean checkForRecursionInLoc(String str) {
		if (str == null) {
			return false;
		}
		if (str.isEmpty() || str.isBlank()) {
			return false;
		}
		
		str = str.toUpperCase();
		SpreadsheetLocation currentTokenLocation;
		if (containsCell(str)) {
			if (isCell(str.trim())) {
				currentTokenLocation = new SpreadsheetLocation(str.trim());
				addLoctoFormulaLocation(currentTokenLocation);
				if (checkForRecursionInLoc(currentTokenLocation)) {
					return true;
				} else {
				}
			}
			String[] Components = SplitStringPlusClean(str.trim().replace("(","").replace(")",""));
			for (String CurrentToken: Components) {
				CurrentToken = CurrentToken.replace(" ", "").replace("-","");
				if (isCell(CurrentToken)) {
					currentTokenLocation = new SpreadsheetLocation(CurrentToken);
					if (checkForRecursionInLoc(currentTokenLocation)) {
						return true;
					}
					addLoctoFormulaLocation(currentTokenLocation);
				}
			}
		}
		return false;
	}
	
	public String ProcessCell(String command, String CellLocStr) {
		Location loc;
		if(command.isBlank()) {
			 loc = new SpreadsheetLocation(CellLocStr);
			 Double retDouble;
			
			 if (getCell(loc) instanceof FormulaCell formulaCell) {
				 retDouble = (formulaCell.getDoubleVal());
				 if (Double.isNaN(retDouble)) {
					 return "#ERROR";
				 } else if (Double.isInfinite(retDouble)) {
					 return "#ERROR";
				 } else {
					 if (!checkForRecursionInLoc((SpreadsheetLocation)loc)) {
					 return Double.toString(retDouble);
					 } else {
						 return "0.0";
					 }
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