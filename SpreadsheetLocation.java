package textExcel;

import java.util.ArrayList;

//Update this file with your own code.

public class SpreadsheetLocation implements Location
{
		private int x;
		private int y;
		public SpreadsheetLocation(String A) {
			int size = 0;
			x = 0;
			y = 0;
			 while (Character.isAlphabetic(A.charAt(size)) && size < A.length()-1) {
		            x = x * 26 + (Character.toUpperCase(A.charAt(size)) - 'A'); 
		            size++;
		        }
		       y = Integer.parseInt(A.substring(size));
		       y -= 1;
		}

    @Override
    public int getRow(){
    	return this.y;
    }
    

    @Override
    public int getCol()
    {
    	return this.x;
    }

}
