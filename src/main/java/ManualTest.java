import java.io.*;
import java.net.*;
import java.util.*;

public class ManualTest {
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;

    public static void main(String[] args) {
        try {
            System.out.println("🚀 Connecting to server...");
            
            // Connect to server
            socket = new Socket("localhost", 9090);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            System.out.println("✅ Connected to server successfully!\n");
            
            // Start tests
            runTests();
            
        } catch (IOException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void runTests() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n===== Test Menu =====");
            System.out.println("1. Register new user");
            System.out.println("2. Show available slots");
            System.out.println("3. Make reservation");
            System.out.println("4. Login");
            System.out.println("5. Show my reservations");
            System.out.println("6. Cancel reservation");
            System.out.println("7. Save data");
            System.out.println("8. Statistics");
            System.out.println("0. Exit");
            System.out.print("Choose test: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            
            switch (choice) {
                case 1:
                    testRegister(scanner);
                    break;
                case 2:
                    testGetSlots(scanner);
                    break;
                case 3:
                    testReserve(scanner);
                    break;
                case 4:
                    testLogin(scanner);
                    break;
                case 5:
                    testGetMyReservations(scanner);
                    break;
                case 6:
                    testCancel(scanner);
                    break;
                case 7:
                    testSave();
                    break;
                case 8:
                    testStats();
                    break;
                case 0:
                    System.out.println("👋 Goodbye!");
                    return;
                default:
                    System.out.println("❌ Invalid choice");
            }
        }
    }

    private static void testRegister(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        String request = "REGISTER:" + username + ":" + password;
        String response = sendRequest(request);
        System.out.println("📝 Result: " + response);
    }

    private static void testGetSlots(Scanner scanner) {
        System.out.print("Enter service name (Manicure, Pedicure, etc): ");
        String service = scanner.nextLine();
        
        String request = "GETSLOTS:" + service;
        String response = sendRequest(request);
        System.out.println("📅 Available slots:\n" + response);
    }

    private static void testReserve(Scanner scanner) {
        System.out.print("Enter slot ID (e.g., SLOT-1): ");
        String slotId = scanner.nextLine();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter service name: ");
        String service = scanner.nextLine();
        
        String request = "RESERVE:" + slotId + ":" + username + ":" + service;
        String response = sendRequest(request);
        System.out.println("🎯 Reservation result: " + response);
    }

    private static void testLogin(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        String request = "LOGIN:" + username + ":" + password;
        String response = sendRequest(request);
        System.out.println("🔐 Login result: " + response);
    }

    private static void testGetMyReservations(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        String request = "GETMYRESERVATIONS:" + username;
        String response = sendRequest(request);
        System.out.println("📋 My reservations:\n" + response);
    }

    private static void testCancel(Scanner scanner) {
        System.out.print("Enter slot ID (e.g., SLOT-1): ");
        String slotId = scanner.nextLine();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        String request = "CANCEL:" + slotId + ":" + username;
        String response = sendRequest(request);
        System.out.println("🗑️ Cancel result: " + response);
    }

    private static void testSave() {
        String request = "SAVE";
        String response = sendRequest(request);
        System.out.println("💾 Save result: " + response);
    }

    private static void testStats() {
        String request = "STATS";
        String response = sendRequest(request);
        System.out.println("📊 Statistics: " + response);
    }

    private static String sendRequest(String request) {
        try {
            out.println(request);
            StringBuilder fullResponse = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                if (line.endsWith("END_OF_RESPONSE")) {
                    fullResponse.append(line, 0, line.length() - "END_OF_RESPONSE".length());
                    break;
                }
                if (line.startsWith("ERROR:")) {
                    return line;
                }
                fullResponse.append(line).append("\n");
            }

            String response = fullResponse.toString().trim();
            if (response.startsWith("RESPONSE:")) {
                return response.substring("RESPONSE:".length()).trim();
            }
            return response;

        } catch (IOException e) {
            return "❌ Connection error: " + e.getMessage();
        }
    }
}