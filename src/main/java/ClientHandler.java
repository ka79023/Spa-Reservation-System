import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler extends Thread {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private static ArrayList<ClientHandler> clients;

    /* مرجع إلى كائن ReservationManager المركزي المسؤول عن منطق النظام. */
    private ReservationManager manager; 

    public ClientHandler(Socket socket, ArrayList<ClientHandler> clients, ReservationManager manager) {
        this.socket = socket;
        this.clients = clients; 
        this.manager = manager; 
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                // قراءة الطلب المرسل من العميل
                String request = in.readLine();
                if (request == null){
                    break;
                } // في حال أغلق العميل الاتصال

                System.out.println("Received: " + request);
                
                // معالجة الطلب عبر الدالة المخصصة
                String response = processRequest(request); 
                
                // إرسال الرد النهائي للعميل
                out.println(response); 
            }
        } catch (IOException e) {
            System.out.println("تم قطع الاتصال مع أحد العملاء.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private String processRequest(String request) {
        String[] parts = request.split(":");
        String command = parts[0];
        
        switch (command) {

            case "REGISTER": // الصيغة: REGISTER:username:password
                if (parts.length < 3) return "ERROR: Invalid register format.";
                String regUsername = parts[1];
                String regPassword = parts[2];
                return "RESPONSE:" + manager.registerUser(regUsername, regPassword) + "\nEND_OF_RESPONSE"; 
                
             case "RESERVE": // الصيغة: RESERVE:slotId:username:serviceName
                if (parts.length < 4) return "ERROR: Invalid reserve format.";
                String slotId = parts[1];
                String resUsername = parts[2];
                String serviceName = parts[3];
                return "RESPONSE:" + manager.attemptReservation(slotId, resUsername, serviceName) + "\nEND_OF_RESPONSE"; 

            case "GETSLOTS": // الصيغة: GETSLOTS:serviceName
                if (parts.length < 2) return "ERROR: Invalid getslots format.";
                serviceName = parts[1];
                return "RESPONSE:" + manager.getAvailableSlots(serviceName) + "\nEND_OF_RESPONSE"; 
                
            // الأوامر الجديدة للمرحلة الثانية
            case "LOGIN": // الصيغة: LOGIN:username:password
                if (parts.length < 3) return "ERROR: Invalid login format.";
                String loginUsername = parts[1];
                String loginPassword = parts[2];
                return "RESPONSE:" + manager.loginUser(loginUsername, loginPassword) + "\nEND_OF_RESPONSE";

            case "GETMYRESERVATIONS": 
    if (parts.length < 2) return "ERROR: Invalid getMyReservations format.";
    String myResUser = parts[1];
    return "RESPONSE:" + manager.getUserReservations(myResUser) + "\nEND_OF_RESPONSE";

            case "CANCEL": // الصيغة: CANCEL:slotId:username
                if (parts.length < 3) return "ERROR: Invalid cancel format.";
                String cancelSlotId = parts[1];
                String cancelUsername = parts[2];
                return "RESPONSE:" + manager.cancelReservation(cancelSlotId, cancelUsername) + "\nEND_OF_RESPONSE";
                
            case "SAVE":
                manager.saveAllData();
                return "RESPONSE:All data saved successfully.\nEND_OF_RESPONSE";
                
            case "STATS":
                return "RESPONSE:Users: " + manager.getUserCount() + ", Slots: " + manager.getSlotCount() + "\nEND_OF_RESPONSE";
                
            default:
                return "ERROR: Unknown command.";
        }
    }
}