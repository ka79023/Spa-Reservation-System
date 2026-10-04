import java.io.*;
import java.net.Socket;

public class ClientTest {
    
    //Network Communication
    // المتغيرات الخاصة بالاتصال بالشبكة (Network Communication)

    private static Socket socket; 
    /* Input Stream
     * لاستقبال الردود من السيرفر */
    private static BufferedReader in;
    /* Output Stream 
     * لإرسال الطلبات إلى السيرفر */
    private static PrintWriter out; 



    public static void main(String[] args) {

        try {
            System.out.println("Attempting to connect to the SPA server...");

            //  إنشاء الاتصال مع السيرفر المحلي على المنفذ 
            socket = new Socket("localhost", 9090); 

            // تهيئة قنوات الإدخال والإخراج
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

                System.out.println(" Successfully connected to the server!\n");

                // Start the GUI after establishing the connection
                 SPA_GUI.main(args);


        } catch (IOException e) {
            // ️ في حال فشل الاتصال، يتم عرض رسالة خطأ للمستخدم
               System.out.println("Failed to connect to the server: " + e.getMessage());
             javax.swing.JOptionPane.showMessageDialog(
                 null,
                    "Connection failed: It seems that the server is not running. Please start the server first.",
                     "Connection Error",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }


     
    public static String Register(String un, String pass) {
        String request = "REGISTER:" + un + ":" + pass;
        return sendRequest(request);
    }

     
    public static String getAvailableSlots(String sn) {
        String request = "GETSLOTS:" + sn;
        return sendRequest(request);
    }

     public static String attemptReservation(String slotId, String un, String serviceName) {
        String request = "RESERVE:" + slotId + ":" + un + ":" + serviceName;
        return sendRequest(request);
    }
     
    public static String login(String un, String pass) {
        String request = "LOGIN:" + un + ":" + pass;
        return sendRequest(request);
    }

    public static String getMyReservations(String un) {
        String request = "GETMYRESERVATIONS:" + un;
        return sendRequest(request);
    }

    public static String cancelReservation(String slotId, String un) {
        String request = "CANCEL:" + slotId + ":" + un;
        return sendRequest(request);
    }

    public static String saveData() {
        String request = "SAVE";
        return sendRequest(request);
    }

    
    //  الدالة الأساسية المسؤولة عن التواصل مع السيرفر


    private static String sendRequest(String request) {
        if (out == null || in == null) {
            return "The client is not connected to the server.";

        }

        try {
            //  إرسال الطلب للسيرفر
            out.println(request);

            // استقبال الرد من السيرفر
            StringBuilder fullResponse = new StringBuilder();
            String line;

            // قراءة جميع الأسطر حتى نصل إلى نهاية الرد
            while ((line = in.readLine()) != null) {
                if (line.endsWith("END_OF_RESPONSE")) {
                    // إزالة العلامة النهائية من النص
                    fullResponse.append(line, 0, line.length() - "END_OF_RESPONSE".length());
                    break;
                }

                // في حال وجود خطأ في الرد، نوقف القراءة مباشرة
                if (line.startsWith("ERROR:")) {
                    return line;
                }

                fullResponse.append(line).append("\n");
            }

            // تنظيف النص من المسافات الزائدة
            String response = fullResponse.toString().trim();

            //  إزالة بادئة RESPONSE: من الرد في حال وجودها
            if (response.startsWith("RESPONSE:")) {
                return response.substring("RESPONSE:".length()).trim();
            } else if (response.startsWith("ERROR:")) {
                return response;
            }

            return response;

        } catch (IOException e) {
            //️ في حال حدوث خطأ أثناء الاتصال (مثل انقطاع الشبكة)
            return "Connection error with the server: " + e.getMessage();

        }
    }
}
