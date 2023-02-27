import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

import java.util.Iterator;

/**
 * This program is designed to read a CSV file containing information about a user's 
 * point balance, where each line of the file represents a payer, the amount of points 
 * they've earned or spent, and the time at which the transaction occurred. It uses this 
 * file to generate the outcome of point transactions. The program takes two inputs: how 
 * many points the user wants to spend and a path to the CSV file mentioned above.The 
 * program spends oldest points first and does not create any new negative balances. 
 * The program then outputs a report of point balances by payer post-spend.
 * 
 * @author Ahmet Ahunbay
 */
public class TransactionAccounter {
	/**
	 * Class that is used to hold information from csv. Each object of this class represents a payer, points,
	 * timestamp triad
	 * @author Ahmet Ahunbay
	 */
	protected static class PointGroup implements Comparable<PointGroup>{
		//relevant fields for triad info
		public String payer;
		public int points;
		public ZonedDateTime date;

		public PointGroup(String payer, int points, ZonedDateTime date) {
			this.points = points;
			this.payer = payer;
			this.date = date;
		}

		//compareTo method for priority queue -- just uses date priority
		@Override
		public int compareTo(PointGroup pg) {
			return this.date.compareTo(pg.date);
		}
	}
	
	/**
	 * Helper method to main that handles transaction and prints report
	 * @param usedPoints points to be deducted
	 * @param csvFile lists all points deductable in form: "payer","points","timestamp"
	 */
	private void manageTransactions(int usedPoints, String csvFile) {
		//priorityqueue used to sort PointGroups by date and access the oldest points
		PriorityQueue<PointGroup> pointQueue = new PriorityQueue<>();
		//tracks payer list
		LinkedHashMap<String, Integer> result = new LinkedHashMap<String, Integer>();
		
		ZonedDateTime exp = ZonedDateTime.now().minusDays(10);
		
		//try/catch to check for invalid file
		try {	
			//counts line number for error messages
			int lineCT = 1;
			//instantiates file reader
			Scanner input = new Scanner(new File(csvFile),"UTF-8");
			//skips header
			input.nextLine();
			
			//iterates through csv file
			while(input.hasNextLine()) {
				lineCT++;
				String line = input.nextLine();	
				//splits payer, points, and timestamp
				String[] components = line.split(",");
				
				//checks for invalid line
				if(components.length < 3) {
					System.out.println("Invalid Line(CSV Line " + lineCT + "). Format: "
							+ "\"payer\",\"points\",\"timestamp\",...");
					continue;
				}
				
				//declares PointGroup constructor arguments
				int linePoints = 0;
				ZonedDateTime date;
				
				//tests for invalid date
				try {
					DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
					date = ZonedDateTime.parse(components[2].replace("\"", ""), formatter);
				} catch(DateTimeParseException e) {
					System.out.println("Invalid date-time value: CSV line " + lineCT);
					continue;
				}
				
				//tests for invalid points value
				try {
					linePoints = Integer.parseInt(components[1].replace("\"", ""));
					
				}catch(NumberFormatException e) {
					System.out.println("Invalid points value: CSV line " + lineCT);
					continue;
				}
				
				if(date.isBefore(exp)){
					System.out.println("Expired ticket: CSV line " + lineCT);
					continue;
				}
				
				if(linePoints <0){
					usedPoints -= linePoints;
					if(!result.containsKey(components[0])) result.put(components[0], 0);
					continue;
				}

				//inserts PointGroup into pointHeap and payer into result
				pointQueue.add(new PointGroup(components[0], linePoints, date));
				result.put(components[0], linePoints + ((result.get(components[0]) == null) ? 0:(result.get(components[0]))));
			
			}
		} catch (IOException e) {
			System.out.println("ERROR: Invalid file name");
			return;
		}
		
		//deducts points from heap until heap has no points or deduction is complete
		while(usedPoints > 0) {
			PointGroup head;
			//checks if heap is empty
			if(pointQueue.size() >0) {
				head = pointQueue.peek();
			} else {
				break;
			}
			
			//either subtracts deducted points from head, or subtracts head points from deducted and removes head
			if(head.points > usedPoints) {
				head.points -= usedPoints;
				result.put(head.payer, result.get(head.payer)-usedPoints);
				break;
			}else {
				usedPoints-=head.points;
				result.put(head.payer, result.get(head.payer)-head.points);
				pointQueue.poll();
			}
		}
		
		//if heap runs out of points and deduction is not complete, error message is given
		if(pointQueue.size() == 0 && usedPoints != 0) {
			System.out.println("Error: Insufficient funds(need " + usedPoints + " more points)");
			return;
		}
		
		//uses keyset and iterator to iterate through result and prints summary
		Set<String> keys = result.keySet();
		Iterator<String> iterator = keys.iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();
			System.out.print(key + ": " + result.get(key));
			if(iterator.hasNext()) {
				System.out.println(",");
			}
		}
		
		//OPTIONAL: prints report that shows when points will expire
		System.out.println("\nWould you like a report on point expiry times? [Y]:yes, [Any Char]:no");
		Scanner input = new Scanner(System.in);
		String choice = input.next();
		input.close();
		
		//unless input is 'Y', nothing will run
		if(choice != null && choice.equals("Y")) {
			//iterates through untouched points in pointQueue -- use pointQueue because it contains the dates
			while(!pointQueue.isEmpty()) {
				//declare variables to hold points, date, and days until expiry
				int points = pointQueue.peek().points;
				//polls queue for iteration
				ZonedDateTime date = pointQueue.poll().date;
				int time = (int) Math.floor(Duration.between(exp, date).toDays());				

				//collapses similar dates into a single line
				while(pointQueue.peek() != null) {					
					ZonedDateTime nextDate = pointQueue.peek().date;
					int nextTime = (int) Math.floor(Duration.between(exp, nextDate).toDays());	
					
					//either sums points and checks next node(similar times), or breaks(dissimilar times)
					if(nextTime == time) {
						//polls queue for iteration
						points += pointQueue.poll().points;
					} else {
						break;
					}
				}
			
				//reports on point status
				if(time == 0) {
					System.out.println(points + " points expire in less than a day");
					continue;
				}
				System.out.println(points + " points expire in " + time + " day" + (time == 1 ? "" : "s"));
				continue;
			}
		}
		
		
	}
	
	/**
	 * Main program to run transaction accounter
	 * @param args must be in format: <points> <csv file>
	 */
	public static void main(String args[]) {
		//tests for defective arg length
		if(args.length <2) {
			System.out.println("Method call format: java TransactionAccounter <points> <csv file>");
			return;
		}
		
		//declares deduct
		int deduct;
		
		//tests for defective args[0]
		try {
			deduct = Integer.parseInt(args[0]);
		}catch(NumberFormatException e) {
			System.out.println("Points must be an integer");
			return;
		}
		if(deduct<0) {
			System.out.println("Point deduction must be positive");
			return;
		}
		
		//instantiates TransactionAccounter and calls instance method to manage transactions
		new TransactionAccounter().manageTransactions(deduct, args[1]);
	}

}
