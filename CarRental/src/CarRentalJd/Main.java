package CarRentalJd;

import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        CarRentalService service = new CarRentalService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            service.displayMenu();
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            if (choice == 1) {
                System.out.println("== For Renting a Car please provide below details ==");
                System.out.print("Enter your name: ");
                String customerName = scanner.nextLine();
                System.out.print("Enter the Car ID you want to rent: ");
                int carId = scanner.nextInt();
                System.out.print("Enter the number of days for rental: ");
                int days = scanner.nextInt();
                service.bookCar(customerName, carId, days);
            } else if (choice == 2) {
                System.out.println("== Return a Car ==");
                System.out.print("Enter the car ID you want to return: ");
                int carId = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter the same name u provided while renting : ");
                String customerName = scanner.nextLine();

                service.returnCar(carId , customerName);
            } else if (choice == 3) {
                service.displayAvailableCars();
            } else if (choice == 4) {
                System.out.println("Exiting the system. Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}
