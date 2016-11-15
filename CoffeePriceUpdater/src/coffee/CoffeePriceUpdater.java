package coffee;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class CoffeePriceUpdater {

	public static void main(String[] args) throws SQLException {
		Scanner keyboard = new Scanner(System.in);
		final String DB_URL = "jdbc:hsqldb:file:CoffeeDB/coffee";
		Connection conn = null;
		try {
			// Create a connection to the database.
			conn = DriverManager.getConnection(DB_URL);
			
			boolean productFound = false;
			while (!productFound) {
				// Get a product number from the user
				System.out.println("Please enter a product number:");
				String prodNum = keyboard.nextLine();
				// Lookup the coffee based on that product number
				productFound = findAndDisplayProduct(conn, prodNum);
				
				// If it does exist, ask the user for a price
				if(productFound){
					System.out.println("Enter the new Price:");
					double price = keyboard.nextDouble();

					// Update the price of the coffee for that product number
					updatePrice(conn, prodNum, price);
				}
			}
			// Display a list of the coffees and their prices in alphabetical
			// order
			displayCoffees(conn);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	public static boolean findAndDisplayProduct(Connection conn, String prodNum) throws SQLException{
		String sqlString = "SELECT Description, ProdNum, Price"
				+ " FROM Coffee"
				+ " WHERE ProdNum = ?";
		
		PreparedStatement stmt = conn.prepareStatement(sqlString);
		
		stmt.setString(1, prodNum);
		
		boolean productFound = false;
		
		ResultSet result = stmt.executeQuery();
		
		if(result.next()){
			System.out.printf("%s %.2f%n", result.getString("Description"), result.getDouble("Price"));
			productFound = true;
		}
		return productFound;
	}
	
	public static void updatePrice(Connection conn, String prodNum, double price) throws SQLException{
		String sql = "UPDATE Coffee "
				+ " SET PRICE = ?"
				+ " WHERE PRODNUM = ?";
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		stmt.setDouble(1, price);
		stmt.setString(2, prodNum);
		
		int rows = stmt.executeUpdate();
		
		System.out.printf("%d rows updated%n", rows);
	}
	
	public static void displayCoffees(Connection conn) throws SQLException{
		String sql = "SELECT Description, Price, ProdNum FROM Coffee ORDER BY Description";
		Statement stmt = conn.createStatement();
		
		ResultSet result = stmt.executeQuery(sql);
		
		while(result.next()){
			System.out.printf("%s %s %.2f%n", result.getString("ProdNum"), 
					result.getString("Description"), result.getDouble("Price"));
		}
	}
}
