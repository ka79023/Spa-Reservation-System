import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    // تم إضافة المدير المركزي هنا
    private static ReservationManager manager = new ReservationManager();
    
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            manager.saveAllData();
            System.out.println("All data saved successfully.");
        }));
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(9090);
            System.out.println(" SPA Reservation Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(" New client connected: " + clientSocket.getInetAddress().getHostAddress());
                
                //تم تمرير نسخة المدير manager إلى ClientHandler
                ClientHandler handler = new ClientHandler(clientSocket, clients, manager);
                clients.add(handler);
                handler.start();
            }

        } catch (IOException e) {
            System.out.println(" Error starting server: " + e.getMessage());
        }
    }
}