import java.util.*;
import java.io.*;


public class ReservationManager {

    /* قاعدة بيانات المستخدمين المسجلين في النظام. */
    private final List<User> userDatabase = new LinkedList<>(); 
    
    /* قاعدة بيانات المواعيد (Time Slots) لجميع الخدمات. */
    private final List<TimeSlot> slotsDatabase = new LinkedList<>(); 
    
     // أسماء الملفات لحفظ البيانات
    private static final String USERS_FILE = "users.dat";
    private static final String SLOTS_FILE = "slots.dat";
    
    /*
      يستدعي دالة initializeSlots لإنشاء جميع المواعيد الممكنة عند تشغيل النظام.
     */
    public ReservationManager() {
        loadData(); // ✅ أولاً نحمل البيانات القديمة
        
        if (slotsDatabase.isEmpty()) {
            initializeSlots(); // ✅ فقط إذا ما فيه بيانات
            System.out.println("✅ Created new slots database");
        }
    }

    // دالة تحميل البيانات من الملفات
    private void loadData() {
        loadUsers();
        loadSlots();
    }
    
       // دالة حفظ البيانات عند إغلاق السيرفر
    public synchronized void saveAllData() {
        saveUsers();
        saveSlots();
        System.out.println("All data saved successfully.");
    }

    private void initializeSlots() {
        String[] services = {"Manicure", "Pedicure", "Massage", "Facial", "Reflexology"};
        String[] therapists = {"T1-Noura", "T2-Alanoud", "T3-Remas", "T4-Khadija", "T5-Lubna"}; 
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        int slotCounter = 0;

        // إنشاء جميع التركيبات الممكنة من الخدمات والمعالجين والأيام والساعات
        for (String s : services) {
            for (String t : therapists) {
                for (String d : days) {
                        String slotId = "SLOT-" + ++slotCounter; 
                        TimeSlot slot = new TimeSlot(slotId, s.trim(), t, d); 
                        slotsDatabase.add(slot); 
                }
            }
        }
    }
    
    // تحميل المستخدمين من الملف
    @SuppressWarnings("unchecked")
    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            List<User> loadedUsers = (List<User>) ois.readObject();
            userDatabase.clear();
            userDatabase.addAll(loadedUsers);
            System.out.println("Users data loaded successfully. Total users: " + userDatabase.size());
        } catch (FileNotFoundException e) {
            System.out.println("No existing users file found. Starting with empty database.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading users data: " + e.getMessage());
        }
    }

    // حفظ المستخدمين في الملف
    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(userDatabase);
            System.out.println("Users data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving users data: " + e.getMessage());
        }
    }

    // تحميل المواعيد من الملف
    @SuppressWarnings("unchecked")
    private void loadSlots() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SLOTS_FILE))) {
            List<TimeSlot> loadedSlots = (List<TimeSlot>) ois.readObject();
            slotsDatabase.clear();
            slotsDatabase.addAll(loadedSlots);
            System.out.println("Slots data loaded successfully. Total slots: " + slotsDatabase.size());
        } catch (FileNotFoundException e) {
            System.out.println("No existing slots file found. Initializing default slots.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading slots data: " + e.getMessage());
        }
    }

    // حفظ المواعيد في الملف
    private void saveSlots() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SLOTS_FILE))) {
            oos.writeObject(slotsDatabase);
            System.out.println("Slots data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving slots data: " + e.getMessage());
        }
    }

    public synchronized String registerUser(String username, String password) {
        boolean newUser = true;
        User user = new User(username, password);

        // البحث في قاعدة البيانات للتحقق من وجود المستخدم
        for (User u : userDatabase) {
            if (u.getUsername().equals(username)) {
                newUser = false;
                break;
            }
        }
        
        if (newUser) {
            userDatabase.add(user);
            saveUsers(); // ✅ حفظ تلقائي بعد التسجيل
            return username + " was successfuly registered!";
        } else {
            return username + " is already registered!";
        }
    }

    /**
     * دالة لجلب جميع المواعيد المتاحة لخدمة محددة.
     * 
     * @param serviceName اسم الخدمة المطلوب عرض مواعيدها.
     * @return قائمة نصية بالمواعيد المتاحة، أو رسالة في حال عدم وجود مواعيد.
     */
    public String getAvailableSlots(String serviceName) {
        String output = "Available Time Slots:\n\n";
        int count = 0;        

        for (TimeSlot slot : slotsDatabase) {
            if (slot.getServiceName().trim().equalsIgnoreCase(serviceName.trim()) && slot.getIsAvailable()) {
                output += String.format("%d. %s (ID: %s)\n", ++count, slot.
                toString(), slot.getSlotId());
            }
        }

        if (count == 0) {
            return "No slots are currently available.";
        } else {
            return output;
        }
    }

    public synchronized String attemptReservation(String slotId, String username, String serviceName) {
        User targetUser = null;
        TimeSlot targetSlot = null;

        //  البحث عن المستخدم في قاعدة البيانات
        for (User user : userDatabase) {
            if (user.getUsername().equals(username)) {
                targetUser = user;
                break;
            }
        }

        if (targetUser == null) {
            return "User not found!";
        }

        //  البحث عن الموعد في قاعدة البيانات
        for (TimeSlot slot : slotsDatabase) {
            if (slot.getSlotId().equals(slotId)) {

                // التحقق من أن الموعد يخص الخدمة نفسها
                if (!slot.getServiceName().equalsIgnoreCase(serviceName.trim())) {
                    return "Error: Slot does not belong to the selected service (" + serviceName + ").";
                }

                // التحقق من أن الموعد غير محجوز مسبقاً
                if (!slot.getIsAvailable()) {
                    return "Sorry, Time slot (" + slotId + ") is already booked.";
                }

                // تنفيذ الحجز
                slot.setIsAvailable(false);
                targetUser.userReservations(slot);
                saveSlots(); // ✅ حفظ تلقائي بعد الحجز
                saveUsers(); // ✅ حفظ تلقائي (لحجوزات المستخدم)
                return "Reservation was successful! Slot: " + slot.getSlotId();
            }
        }

        // في حال لم يتم العثور على الموعد المطلوب
        return "Error: Slot ID (" + slotId + ") was not found.";
    }
    
 // دوال المرحلة الثانية
    public synchronized String loginUser(String username, String password) {
        for (User user : userDatabase) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return "Login successful! Welcome back " + username;
            }
        }
        return "Login failed: Invalid username or password.";
    }

    public String getUserReservations(String username) {
        for (User user : userDatabase) {
            if (user.getUsername().equals(username)) {
                List<TimeSlot> reservations = user.getReservations();
                if (reservations.isEmpty()) {
                    return "You have no reservations.";
                }
                
                StringBuilder output = new StringBuilder("Your Reservations:\n\n");
                int count = 0;
                for (TimeSlot slot : reservations) {
                    output.append(String.format("%d. %s (ID: %s)\n", ++count, slot.toString(), slot.getSlotId()));
                }
                return output.toString();
            }
        }
        return "User not found.";
    }

    public synchronized String cancelReservation(String slotId, String username) {
        User targetUser = null;
        for (User user : userDatabase) {
            if (user.getUsername().equals(username)) {
                targetUser = user;
                break;
            }
        }
        
        if (targetUser == null) return "User not found!";
        
        TimeSlot reservationToCancel = null;
        for (TimeSlot slot : targetUser.getReservations()) {
            if (slot.getSlotId().equals(slotId)) {
                reservationToCancel = slot;
                break;
            }
        }
        
        if (reservationToCancel == null) return "Reservation not found!";
        
        for (TimeSlot slot : slotsDatabase) {
            if (slot.getSlotId().equals(slotId)) {
                slot.setIsAvailable(true);
                break;
            }
        }
        
        targetUser.getReservations().remove(reservationToCancel);
        saveSlots(); // ✅ حفظ تلقائي بعد الإلغاء
        saveUsers(); // ✅ حفظ تلقائي
        return "Reservation cancelled successfully!";
    }

    public int getUserCount() {
        return this.userDatabase.size();
    }

    public int getSlotCount() {
        return this.slotsDatabase.size();
    }
}
