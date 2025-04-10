package textExcel;

import java.io.FileNotFoundException;
import java.util.Scanner;

// Update this file with your own code.

public class TextExcel
{

	public static void main(String[] args) {
		Scanner console = new Scanner(System.in);
		Spreadsheet Spsh1 = new Spreadsheet();
		boolean bool = true;
		while (bool) {
			//Maybe a system where you could change which Spreadsheet you want to close, so would need a name?
			String a = console.next();
			if (!a.equals("quit")) {
				Spsh1.processCommand(a);
			} else {
				bool = false;
			}
		}
	}
}
