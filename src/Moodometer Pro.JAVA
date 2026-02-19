/**
 * Project: Moodometer Pro 3.0 (Sentiment-Based Analysis & Persistence)
 * Author: Deepak
 * Description: An intelligent Java application that uses text analysis to 
 * determine user mood and persists historical data in a MySQL database.
 */

import java.sql.*;
import java.util.Scanner; [cite_start]// [cite: 2, 44]

public class MoodometerPro {

    // --- DATABASE CONFIGURATION ---
    // Update these credentials with your local MySQL settings
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mood_db";
    private static final String DB_USER = "root"; 
    private static final String DB_PASS = "yourpassword"; // Update this with your actual password

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); [cite_start]// [cite: 6, 47]
        setupDatabase(); 

        System.out.println("==========================================");
        System.out.println("       MOODOMETER PRO - SYSTEM ACTIVE     ");
        System.out.println("==========================================");

        while (true) {
            System.out.println("\n[1] Analyze & Log Mood");
            System.out.println("[2] View Emotional Trends (History)");
            System.out.println("[3] Exit System");
            System.out.print("Select Action: ");
            
            int choice = sc.nextInt();
            sc.nextLine(); 

            [cite_start]// Logic control inspired by multi-branch decision structures [cite: 17, 22, 27, 32]
            switch (choice) {
                case 1:
                    processMood(sc);
                    break;
                case 2:
                    fetchHistory();
                    break;
                case 3:
                    System.out.println("System Shutting Down. Stay Positive!");
                    return;
                default:
                    System.out.println("Invalid Selection.");
            }
        }
    }

    private static void setupDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS mood_history ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "user_input TEXT NOT NULL, "
                + "mood_score VARCHAR(20), "
                + "entry_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Note: DB Connection failed. Ensure MySQL is running and credentials are correct.");
        }
    }

    private static void processMood(Scanner sc) {
        System.out.println("\nDescribe your current state (e.g., 'I had a great day'):");
        String input = sc.nextLine();
        
        [cite_start]// Sentiment analysis using conditional keyword matching [cite: 51, 56, 60]
        String mood;
        String lowerInput = input.toLowerCase();
        if (lowerInput.contains("happy") || lowerInput.contains("great") || lowerInput.contains("awesome")) {
            mood = "POSITIVE";
        } else if (lowerInput.contains("sad") || lowerInput.contains("stressed") || lowerInput.contains("bad")) {
            mood = "NEGATIVE";
        } else {
            mood = "NEUTRAL";
        }

        System.out.println(">>> Detected Mood: " + mood);
        saveToDatabase(input, mood);
    }

    private static void saveToDatabase(String input, String mood) {
        String insertSQL = "INSERT INTO mood_history (user_input, mood_score) VALUES (?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            
            pstmt.setString(1, input);
            pstmt.setString(2, mood);
            pstmt.executeUpdate();
            System.out.println("Data successfully synced to MySQL database.");
            
        } catch (SQLException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private static void fetchHistory() {
        System.out.println("\n--- RECENT MOOD HISTORY ---");
        String query = "SELECT * FROM mood_history ORDER BY id DESC LIMIT 5";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            System.out.printf("%-20s | %-10s | %s\n", "Timestamp", "Mood", "Input Note");
            System.out.println("----------------------------------------------------------");
            
            while (rs.next()) {
                System.out.printf("%-20s | %-10s | %s\n", 
                    rs.getTimestamp("entry_time"), 
                    rs.getString("mood_score"), 
                    rs.getString("user_input"));
            }
        } catch (SQLException e) {
            System.out.println("Could not retrieve history: " + e.getMessage());
        }
    }
}