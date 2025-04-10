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
	
	public String ProcessCell(String command, String CellLocStr) {
		Location loc;
		if(command.isBlank()) {
			 loc = new SpreadsheetLocation(CellLocStr);
				return getCell(loc).fullCellText();
		} else {
			String[] commandComponents = command.split("=",2);
			loc = new SpreadsheetLocation(commandComponents[0].trim());
			System.out.println("componenet 1: " + commandComponents[0].trim());
			setCell(loc, commandComponents[1].trim());
			System.out.println("componenet 2: " + commandComponents[1].trim());
			return getGridText();
		}
	}
	
	
	public String ProcessCell(String command, SpreadsheetLocation loc) {
		if(command.isBlank()) {
				return getCell(loc).fullCellText();
		} else {
			String[] commandComponents = command.split("=",2);
			loc = new SpreadsheetLocation(commandComponents[0].trim());
			System.out.println("componenet 1: " + commandComponents[0].trim());
			setCell(loc, commandComponents[1].trim());
			System.out.println("componenet 2: " + commandComponents[1].trim());
			return getGridText();
		}
	}

	public void setCell(Location loc, String textToSet) {
		//textToSet == null -> emptyCell
		//sheet[loc.getCol()][loc.getRow()]
		Cell cell;
		if (textToSet.contains("\"")) {cell = new TextCell(textToSet);}
		else if (textToSet.contains(")") || textToSet.contains("+") || isCell(textToSet.toUpperCase())) {
			cell = new FormulaCell(textToSet);
			currentFormulaLocation = (SpreadsheetLocation) loc;
			Double ans = calcSingle(textToSet);
			
			if (ans != Double.POSITIVE_INFINITY) {
				((FormulaCell) cell).setAnswer(ans);
			} else {
				((FormulaCell) cell).setAnswer(Double.POSITIVE_INFINITY);
			}
			
			}
		else if(!textToSet.trim().replace("-","").matches("[^0-9]")&&textToSet.contains("%")) {cell = new PercentCell(textToSet);}
		else if (!textToSet.replace("-","").matches("[^0-9]")) {cell = new ValueCell(textToSet);}
		
		else {cell = new EmptyCell(); System.out.println("EmptyCell with " + textToSet);}
		sheet[loc.getCol()][loc.getRow()] = cell;
		System.out.println(cell.getClass());
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
	            		Double val = calcSingle(sheet[j][i].fullCellText());
	            		grid += Double.toString(val).length() > 10 ? Double.toString(val).substring(0,10)
	            											: Double.toString(val) + " ".repeat(10 - Double.toString(val).length());
	            	} else {
	                grid += "|" + sheet[j][i].abbreviatedCellText();
	            	}
	            }
	        }
	        grid += "|\n"; 
	    }
	    return grid;
	}
	
	private String average(String beg, String end) {
		if (beg.equalsIgnoreCase(end)) {
			return ProcessCell("",end.toUpperCase());
		}
		if (beg.charAt(0) == end.charAt(0)) {
			int difference = Integer.parseInt(end.substring(1)) - Integer.parseInt(beg.substring(1));
			Double avg = 0.0;
			SpreadsheetLocation loc;
			for (int i = Integer.parseInt(beg.substring(1)); i < Integer.parseInt(end.substring(1)) + difference; i++)  {
				loc = new SpreadsheetLocation(Character.toString(beg.charAt(0)) + i);
				String ProcessedCell = ProcessCell("",loc);
				if (ProcessedCell.matches("-?\\d+(\\.\\d+)?")) {
					avg += Double.parseDouble(ProcessedCell);
				}
			}
			return Double.toString(avg);
		} else {
			return "oh no";
		}
	}

	public String Sumative(String beg, String end) {
		int i = Integer.parseInt(beg.replaceAll("[^0-9.]", ""))-Integer.parseInt(end.replaceAll("[^0-9.]", ""));
		return Double.toString(Double.parseDouble(average(beg,end))*i);
	}
	
	public double calcSingle(String str) {
		str = str.replaceAll("[()]","").replace(" ", "");
		System.out.println("string after replacing:\t" + str);
		if (str.toUpperCase().trim().startsWith("SUM")) {
			str = str.trim().toUpperCase();
			return Double.parseDouble(average(str.replace("AVG","").split("-")[0],str.split("-")[1]));
		}
		if (str.toUpperCase().trim().startsWith("SUM")) {
			str = str.trim().toUpperCase();
			return Double.parseDouble(Sumative(str.replace("SUM","").split("-")[0],str.split("-")[1]));
		}
		ArrayList<String> Components = new ArrayList<String>();
		Components = addAll(SplitStringPlusClean(str));
		double ReturnValue = 0.0
				,CurrentDouble = Double.NaN;
		String CurrentToken;
		char Opperand;
		for (int i = 0; i < Components.size()-1; i++) {
			CurrentToken = Components.get(i);
			System.out.print(i + ":" + CurrentToken);
			if (isCell(CurrentToken) || containsCell(CurrentToken)) {
			    String cellContent = processCommand(CurrentToken);
			    if (cellContent.trim().startsWith("(")) {
			        cellContent = cellContent.replaceAll("[()]", "").trim();
			        double evaluated = calcSingle(cellContent);
			        Components.set(i, String.valueOf(evaluated));
			    } else {
			        Components.set(i, cellContent.replaceAll("[()]", "").trim());
			    }
			}
			if (!CurrentToken.isBlank()) {
				if (CurrentToken.contains("*")) { //[a /^* b] --> [c]
					Components.set(i,Double.toString(doubleSimpleCalculation(Components.get(i-1),Components.get(i+1) , '*')));
					System.out.println("\n multiplied:\t" + Components.get(i-1) + "*"  + Components.get(i+1) + " to get:->"+ Components.get(i));
					Components.remove(i+1);
					Components.remove(i-1);
					i--;
				} else if (CurrentToken.contains("/")) { //[a /^* b] --> [c]
					Components.set(i,Double.toString(doubleSimpleCalculation(Components.get(i-1),Components.get(i+1) , '/')));
					System.out.println("\n divided:\t" + Components.get(i-1) + "/"  + Components.get(i+1) + " to get:->"+ Components.get(i));
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
				if(currentToken.matches("-?\\d+(\\.\\d+)?")) {
				ReturnValue += Double.parseDouble(currentToken);
				}
			}
		}
		currentFormulaLocation = new SpreadsheetLocation("Z202");
		FormulaLocations = null;
		return ReturnValue;
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
			System.out.println("string after Spliting and cleaning" + str);
			return str.split("\\$");
			
		} else {
			 String[] s = {str};
			return s;
		}
	}

	public double doubleSimpleCalculation(String Ex1, String Ex2, char Opperand) {
		SpreadsheetLocation loc;
		if (isCell(Ex1) || containsCell(Ex1)) {
			loc = new SpreadsheetLocation(Ex1);
			System.out.print("\nbefore : " + Ex1);
			Ex1 = Double.toString(RecursiveProcesscell(loc));
			System.out.println("after : " + Ex1 + "\n");
		}
		if (isCell(Ex2) || containsCell(Ex2)) {
			System.out.print("\nbefore : " + Ex2);
			loc = new SpreadsheetLocation(Ex2);
			Ex2 = Double.toString(RecursiveProcesscell(loc));
			System.out.println("after : " + Ex2 + "\n");
		}
		switch (Opperand) {
		case '/':
			if (Ex2.equals("0") || Ex2.equals("0.0")) {
				return Ex1.contains("-") ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
			}
			return Double.parseDouble(Ex1) / Double.parseDouble(Ex2);
		case '*':
			return Double.parseDouble(Ex1) * Double.parseDouble(Ex2); 
		case '+':
			return Double.parseDouble(Ex1) + Double.parseDouble(Ex2); 
			default:
				return 0.0;
		}
	}
	public ArrayList<SpreadsheetLocation> FormulaLocations = null;
	public double RecursiveProcesscell(Location CellLoc) {
		if (FormulaLocations != null) {
			for (SpreadsheetLocation x: FormulaLocations) {
				if (CellLoc.equals(x)) {
					return Double.POSITIVE_INFINITY;
				}
			}
		} else if (CellLoc.equals(currentFormulaLocation)) {
			return Double.POSITIVE_INFINITY;
		} else {
			if (getCell(CellLoc) instanceof FormulaCell) {
				return calcSingle(getCell(CellLoc).fullCellText().replaceAll("[()]", "").replace(" ", ""));
			} else if (getCell(CellLoc) instanceof RealCell rcell) {
				return Double.parseDouble(rcell.fullCellText());
			}
		}
		System.out.println("you messed up bub");
		return 024.96;
		
	}
}