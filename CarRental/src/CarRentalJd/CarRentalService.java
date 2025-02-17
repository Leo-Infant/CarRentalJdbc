package CarRentalJd;
import java.sql.*;
import java.util.Scanner;

public class CarRentalService {
    static final String DB_URL = "jdbc:mysql://localhost:3306/carrentaldb";
    static final String USER = "root";  // Adjust as per your MySQL username
    static final String PASS = "Lithi@123";  // Adjust as per your MySQL password

    public void displayMenu() {
        System.out.println("===== Welcome to our Car Rental System =====");
        System.out.println("1. Book a Car");
        System.out.println("2. Return a Booked Car");
        System.out.println("3. Available Cars");
        System.out.println("4. Exit");
    }

    // Book a car method
    public void bookCar(String customerName, int carId, int days) {
        Scanner input = new Scanner(System.in);
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
    
            // Check if the car is available
            String checkCarAvailabilitySql = "SELECT no_of_available_car, price_per_day FROM cars WHERE car_id = ?";
            stmt = conn.prepareStatement(checkCarAvailabilitySql);
            stmt.setInt(1, carId);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                int availableCars = rs.getInt("no_of_available_car");
                double pricePerDay = rs.getDouble("price_per_day");
    
                if (availableCars > 0) {
                    System.out.println("Confirm rent (Y/N): ");
                    
                    String confirmation = input.nextLine().toUpperCase();
                    
                    if (confirmation.equals("Y")) {
                        // Calculate total price
                        double totalPrice = pricePerDay * days;
                        
                        String selectSql = "SELECT brand , model FROM cars WHERE car_id = ? ";
                        stmt = conn.prepareStatement(selectSql);
                        stmt.setInt(1 , carId);
                        rs = stmt.executeQuery();
                        String brandName = null;
                        String model = null;
                        if(rs.next()){
                            brandName = rs.getString("brand");
                            model = rs.getString("model");
                        }
                        // Insert booking details only if the confirmation is "Y"
                        String bookCarSql = "INSERT INTO bookings (customerName , car_id , days, total_price, is_returned) VALUES (?,?, ?, ?, FALSE)";
                        stmt = conn.prepareStatement(bookCarSql); // Enable key retrieval
                        stmt.setString(1,customerName);   // cutomserName
                        stmt.setInt(2, carId);            // car_id
                        stmt.setInt(3, days);             // days
                        stmt.setDouble(4, totalPrice);    // total_price
                        stmt.executeUpdate();

                        // Update car availability only after confirmation
                        String updateCarSql = "UPDATE cars SET no_of_available_car = no_of_available_car - 1 WHERE car_id = ?";
                        stmt = conn.prepareStatement(updateCarSql);
                        stmt.setInt(1, carId);
                        stmt.executeUpdate();
                        
                        System.out.println("Brand: " + brandName);
                        System.out.println("Model: " + model);
                        System.out.println("Total price: Rs " + totalPrice);
                        System.out.println("Car booked successfully!");
                    }
                    
                    else if (confirmation.equals("N")) {
                        // If the booking is cancelled
                        System.out.println("Car booking is cancelled.");
                    } 
                    else {
                        // For invalid inputs
                        System.out.println("Invalid input. Car booking is cancelled.");
                    }
                }
                 else {
                    System.out.println("Car is not available for booking.");
                }
                
            } 
            else {
                System.out.println("Car not found.");
            }
    
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    // Return a booked car method
    public void returnCar(int carId , String customerName) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
    
            // Check if the car has been booked by the specified customer and not already returned
            String checkBookingSql = "SELECT is_returned FROM bookings WHERE car_id = ? AND customerName = ? AND is_returned = FALSE";
            stmt = conn.prepareStatement(checkBookingSql);
            stmt.setInt(1, carId);
            stmt.setString(2, customerName);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                // Mark the car as returned
                String updateBookingSql = "UPDATE bookings SET is_returned = TRUE WHERE car_id = ? AND customerName = ?";
                stmt = conn.prepareStatement(updateBookingSql);
                stmt.setInt(1, carId);
                stmt.setString(2, customerName);
                stmt.executeUpdate();
    
                // Update car availability
                String updateCarSql = "UPDATE cars SET no_of_available_car = no_of_available_car + 1 WHERE car_id = ?";
                stmt = conn.prepareStatement(updateCarSql);
                stmt.setInt(1, carId);
                stmt.executeUpdate();
    
                // Notify the customer
                System.out.println("Car returned successfully by customer :" +customerName);
            } else {
                System.out.println("No active booking found for this car with the specified customer ID, or the car has already been returned.");
            }
    
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
    
    // Display available cars
    public void displayAvailableCars() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Query to get available cars
            String sql = "SELECT * FROM cars WHERE no_of_available_car > 0";
            stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Available Cars:");
            while (rs.next()) {
                System.out.println("Car ID: " + rs.getString("car_id"));
                System.out.println("Brand: " + rs.getString("brand"));
                System.out.println("Model: " + rs.getString("model"));
                System.out.println("Price per day: $" + rs.getDouble("price_per_day"));
                System.out.println("Available cars: " + rs.getInt("no_of_available_car"));
                System.out.println("----------------------------");
            }

        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}