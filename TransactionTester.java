import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;

import com.opencsv.CSVWriter;

/**
 * Basic tester methods written to ensure a practical level of functionality
 * @author Ahmet Ahunbay
 *
 */
public class TransactionTester {
	
	/**
	 * Tests empty csv file
	 * @return true if expected output, false otherwise
	 */
	public static boolean noPayerTest() {
		try {
			//instantiates file and output stream
			String file = "noPayer.csv";
			FileWriter output = new FileWriter(file);
			CSVWriter writer = new CSVWriter(output);
			
			//only writes header
	        writer.writeNext(new String[] {"payer", "points", "timestamp"});
	        
	        //closes output stream
	        writer.close();
	        output.close();
	        
	        //creates input stream to catch output
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PrintStream ps = new PrintStream(baos);
	        PrintStream old = System.out;
	        System.setOut(ps);
	        
	        //calls function
	        TransactionAccounter.main(new String[] {"1000", file});
	        
	        //collects output in String
	        System.out.flush();
	        System.setOut(old);
	        String consoleOutput = baos.toString();
	        
	        //compares string
			if(!consoleOutput.trim().equals("Error: Insufficient funds(need 1000 more points)")) {
				return false;
			}
			//if test passes, returns true
	        return true;
		}catch(Exception e) {
			return false;
		}
	}
	
	/**
	 * Tests to see if code works with insufficient funds and perfectly sufficient funds
	 * @return
	 */
	public static boolean fundTest() {
		try {
			//instantiates file and output stream
			String file = "insufficient.csv";	
			FileWriter output = new FileWriter(file);
			CSVWriter writer = new CSVWriter(output);
	
			//writes csv
	        writer.writeNext(new String[] {"payer", "points", "timestamp"});
	        writer.writeNext(new String[] {"Mcdonalds", "200", "2020-10-31T10:00:00Z"});
	        writer.writeNext(new String[] {"Popeyes", "300", "2020-10-31T10:00:00Z"});
	        writer.writeNext(new String[] {"Starbucks", "-400", "2020-10-30T10:00:00Z"});
	        writer.writeNext(new String[] {"KFC", "500", "2020-10-31T10:00:00Z"});
	        
	        //closes output stream
	        writer.close();
	        output.close();
	        
	      //creates input stream to catch output
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PrintStream ps = new PrintStream(baos);
	        PrintStream old = System.out;
	        System.setOut(ps);
	        
	        //insufficient funds
	        TransactionAccounter.main(new String[] {"700", file});
	        //perfectly sufficient funds
	        TransactionAccounter.main(new String[] {"600", file});
	        
	        //collects output in String
	        System.out.flush();
	        System.setOut(old);
	        String consoleOutput = baos.toString();
	        //checks if output is expected
			if(!consoleOutput.trim().equals("Error: Insufficient funds(need 100 more points)\r\n"
					+ "\"Mcdonalds\": 0,\r\n"
					+ "\"Popeyes\": 0,\r\n"
					+ "\"Starbucks\": 0,\r\n"
					+ "\"KFC\": 0")) {
				return false;
			}
			
	        return true;
		}catch(Exception e) {
			return false;
		}
	}
	
	/**
	 * Checks a variety of incorrect inputs, including incorrect args and incorrect lines
	 * @return true if output is expected, false otherwise
	 */
	public static boolean badInputTest() {
		try {
			//instantiates file and output stream
			String file = "badLines.csv";
			FileWriter output = new FileWriter(file);
			CSVWriter writer = new CSVWriter(output);

			//writes into csv
	        writer.writeNext(new String[] {"payer", "points", "timestamp"});
	        writer.writeNext(new String[] {"KFC", "100", "10/20/21"});
	        writer.writeNext(new String[] {"KFC", "one hundred", "2020-10-31T10:00:00Z"});
	        writer.writeNext(new String[] {"KFC", "one hundred"});
	        writer.writeNext(new String[] {"KFC", "100", "2020-10-31T10:00:00Z"});
	        
	        //closes output stream
	        writer.close();
	        output.close();
	        
	        //opens input stream to catch output
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PrintStream ps = new PrintStream(baos);
	        PrintStream old = System.out;
	        System.setOut(ps);
	        
	        //calls on empty args
	        TransactionAccounter.main(new String[] {});
	        //only file name
	        TransactionAccounter.main(new String[] {"file.csv"});
	        //only points
	        TransactionAccounter.main(new String[] {"132"});
	        //negative points
	        TransactionAccounter.main(new String[] {"-100", "file.csv"});
	        //nonexistent file
	        TransactionAccounter.main(new String[] {"100", "file.csv" });
	        //file with messed up line format
	        TransactionAccounter.main(new String[] {"100", "badLines.csv" });
	        
	        //catches output in string
	        System.out.flush();
	        System.setOut(old);
	        String consoleOutput = baos.toString();
	        
	        //compares to expected output 
			if(!consoleOutput.trim().equals("Method call format: java TransactionAccounter <points> <csv file>\r\n"
					+ "Method call format: java TransactionAccounter <points> <csv file>\r\n"
					+ "Method call format: java TransactionAccounter <points> <csv file>\r\n"
					+ "Point deduction must be positive\r\n"
					+ "ERROR: Invalid file name\r\n"
					+ "Invalid date-time value: CSV line 2\r\n"
					+ "Invalid points value: CSV line 3\r\n"
					+ "Invalid Line(CSV Line 4). Format: \"payer\",\"points\",\"timestamp\",...\r\n"
					+ "\"KFC\": 0")) {
				return false;
			}
	        return true;
			}catch(Exception e) {
				e.printStackTrace();
				return false;
			}
	}
	
	/**
	 * tests that report groups points by payer correctly and that payer order is maintained
	 * @return true if output is expected, false otherwise
	 */
	public static boolean collapseTest() {
		try {
			//instantiates file and output stream
			String file = "insufficient.csv";
			FileWriter output = new FileWriter(file);
			CSVWriter writer = new CSVWriter(output);		
			
			//writes into csv
	        writer.writeNext(new String[]{"payer", "points", "timestamp"});
	        writer.writeNext(new String[] {"Mcdonalds", "200", "2020-10-01T10:00:00Z"});
	        writer.writeNext(new String[] {"Popeyes", "300", "2020-05-02T10:00:00Z"});
	        writer.writeNext(new String[] {"Starbucks", "-400", "2020-10-03T10:00:00Z"});
	        writer.writeNext(new String[] {"KFC", "500", "2020-10-04T10:00:00Z"});
	        writer.writeNext(new String[] {"Starbucks", "-400", "2020-10-05T10:00:00Z"});
	        writer.writeNext(new String[] {"KFC", "700", "2020-10-06T10:00:00Z"});
	        writer.writeNext(new String[] {"Mcdonalds", "800", "2020-10-07T10:00:00Z"});
	        writer.writeNext(new String[] {"Popeyes", "300", "2020-10-08T10:00:00Z"});
	        writer.writeNext(new String[] {"Mcdonalds", "200", "2020-08-07T10:00:00Z"});
	        writer.writeNext(new String[] {"Mcdonalds", "200", "2020-05-07T10:00:00Z"});
	        
	        //closes output stream
	        writer.close();
	        output.close();
	        
	        //instantiates input stream to catch output
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PrintStream ps = new PrintStream(baos);
	        PrintStream old = System.out;
	        System.setOut(ps);
	        
	        //calls method on csv
	        TransactionAccounter.main(new String[] {"1700", file});
	        
	        //catches output into String
	        System.out.flush();
	        System.setOut(old);
	        String consoleOutput = baos.toString();
	        
	        //compares with expected output
			if(!consoleOutput.trim().equals("\"Mcdonalds\": 400,\r\n"
					+ "\"Popeyes\": 300,\r\n"
					+ "\"Starbucks\": 0,\r\n"
					+ "\"KFC\": 0")) {
				return false;
			}
			
	        return true;
			}catch(Exception e) {
				return false;
			}
	}

	/**
	 * Calls all tester methods
	 * @param args not used
	 */
	public static void main(String args[]) {
		System.out.println("TEST 1(No Payer Test): " + (noPayerTest() ? "PASSED" : "FAILED"));
		System.out.println("TEST 2(Fund Test): " + (fundTest() ? "PASSED" : "FAILED"));
		System.out.println("TEST 3(Bad Input Test): " + (badInputTest() ? "PASSED" : "FAILED"));
		System.out.println("TEST 4(Collapse Test): " + (collapseTest() ? "PASSED" : "FAILED"));
		
	}
}
