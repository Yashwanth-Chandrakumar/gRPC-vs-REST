package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class Main {

    // JDBC database URL, username, and password - Replace with your actual database credentials
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/students"; // Replace with your database URL
    private static final String USER = "root";            // Replace with your MySQL username
    private static final String PASSWORD = "pappu1031";        // Replace with your MySQL password

    // SQL statement to insert a student record
    private static final String INSERT_STUDENT_SQL = "INSERT INTO students (" +
            "first_name, last_name, middle_name, date_of_birth, gender, email, " +
            "phone_number, address_line1, address_line2, city, state, zip_code, " +
            "country, major, minor, gpa, credits_completed, enrollment_status, " +
            "graduation_date, advisor_id, financial_aid_status, scholarship_name, " +
            "room_number, building, start_date, end_date, grade_level) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String[] FIRST_NAMES = {"Alice", "Bob", "Charlie", "David", "Emily", "Fiona", "George", "Hannah", "Isaac", "Jane"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Brown", "Davis", "Wilson", "Garcia", "Rodriguez", "Williams", "Jones", "Miller"};
    private static final String[] MIDDLE_NAMES = {"Marie", "William", "Jane", "Anthony", "Lynn", "Michael", "Elizabeth", "Robert", "Mary", "David"};
    private static final String[] GENDERS = {"Male", "Female", "Other"};
    private static final String[] MAJORS = {"Computer Science", "Biology", "Engineering", "Chemistry", "Physics", "Economics", "Psychology", "Sociology", "History", "Nursing"};
    private static final String[] MINORS = {"Mathematics", "Statistics", "Finance", "Sociology", "Political Science", "Public Health", "Physics", "Chemistry", "Biology", "Computer Science"};
    private static final String[] ENROLLMENT_STATUSES = {"Enrolled", "On Leave", "Graduated"};
    private static final String[] FINANCIAL_AID_STATUSES = {"None", "Applied", "Approved", "Disbursed"};
    private static final String[] SCHOLARSHIP_NAMES = {"Presidential Scholarship", "Merit Scholarship", "State Grant", "University Scholarship", "Alumni Scholarship", "Health Sciences Scholarship", "National Merit Scholarship", "Trustee Scholarship", "Dean's Scholarship", "Faculty Scholarship"};
    private static final String[] ROOM_NUMBERS = {"101", "202", "301", "404", "502", "606", "703", "808", "909", "1000"};
    private static final String[] BUILDINGS = {"Sierra Hall", "Redwood Hall", "Cedar Hall", "Oak Hall", "Pine Hall", "Liberty Hall", "Business Building", "Social Sciences Bldg", "Humanities Hall", "Medical Building"};
    private static final String[] GRADE_LEVELS = {"Freshman", "Sophomore", "Junior", "Senior", "Graduate"};
    private static final String[] CITIES = {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose"};
    private static final String[] STATES = {"NY", "CA", "IL", "TX", "AZ", "PA", "TX", "CA", "TX", "CA"};
    private static final String[] COUNTRIES = {"USA"};
    private static final int ADVISOR_ID_START = 101; // Starting advisor ID
    private static final int ADVISOR_ID_RANGE = 10;   // Range of advisor IDs

    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            // Load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver"); // Corrected driver class name

            // Establish the connection
            connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            System.out.println("Connected to the database successfully!");

            // Prepare the SQL statement
            preparedStatement = connection.prepareStatement(INSERT_STUDENT_SQL);

            // Create a Random object for generating random data
            Random random = new Random();

            // Loop to insert 100,000 records
            final int numRecords = 100000;
            final int batchSize = 1000; // Insert records in batches

            for (int i = 0; i < numRecords; i++) {
                // Generate random data for each student
                String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
                String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
                String middleName = MIDDLE_NAMES[random.nextInt(MIDDLE_NAMES.length)];
                java.sql.Date dateOfBirth = generateRandomDate(random, 1980, 2005); // Students born between 1980 and 2005
                String gender = GENDERS[random.nextInt(GENDERS.length)];
                String email = generateRandomEmail(firstName, lastName, i); // Ensure unique email
                String phoneNumber = generateRandomPhoneNumber(random);
                String addressLine1 = generateRandomAddressLine1(random);
                String addressLine2 = generateRandomAddressLine2(random);
                String city = CITIES[random.nextInt(CITIES.length)];
                String state = STATES[random.nextInt(STATES.length)];
                String zipCode = generateRandomZipCode(random);
                String country = COUNTRIES[random.nextInt(COUNTRIES.length)];
                String major = MAJORS[random.nextInt(MAJORS.length)];
                String minor = MINORS[random.nextInt(MINORS.length)];
                double gpa = 2.0 + (random.nextDouble() * 2.0); // GPA between 2.0 and 4.0
                int creditsCompleted = random.nextInt(120); // Up to 120 credits
                String enrollmentStatus = ENROLLMENT_STATUSES[random.nextInt(ENROLLMENT_STATUSES.length)];
                java.sql.Date graduationDate = generateRandomDate(random, 2024, 2028); // Graduation between 2024 and 2028
                int advisorId = ADVISOR_ID_START + random.nextInt(ADVISOR_ID_RANGE);
                String financialAidStatus = FINANCIAL_AID_STATUSES[random.nextInt(FINANCIAL_AID_STATUSES.length)];
                String scholarshipName = SCHOLARSHIP_NAMES[random.nextInt(SCHOLARSHIP_NAMES.length)];
                String roomNumber = ROOM_NUMBERS[random.nextInt(ROOM_NUMBERS.length)];
                String building = BUILDINGS[random.nextInt(BUILDINGS.length)];
                java.sql.Date startDate = generateRandomDate(random, 2020, 2023);
                java.sql.Date endDate = generateRandomDate(random, 2024, 2028);
                String gradeLevel = GRADE_LEVELS[random.nextInt(GRADE_LEVELS.length)];

                // Set the parameters for the prepared statement
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, middleName);
                preparedStatement.setDate(4, dateOfBirth);
                preparedStatement.setString(5, gender);
                preparedStatement.setString(6, email);
                preparedStatement.setString(7, phoneNumber);
                preparedStatement.setString(8, addressLine1);
                preparedStatement.setString(9, addressLine2);
                preparedStatement.setString(10, city);
                preparedStatement.setString(11, state);
                preparedStatement.setString(12, zipCode);
                preparedStatement.setString(13, country);
                preparedStatement.setString(14, major);
                preparedStatement.setString(15, minor);
                preparedStatement.setDouble(16, gpa);
                preparedStatement.setInt(17, creditsCompleted);
                preparedStatement.setString(18, enrollmentStatus);
                preparedStatement.setDate(19, graduationDate);
                preparedStatement.setInt(20, advisorId);
                preparedStatement.setString(21, financialAidStatus);
                preparedStatement.setString(22, scholarshipName);
                preparedStatement.setString(23, roomNumber);
                preparedStatement.setString(24, building);
                preparedStatement.setDate(25, startDate);
                preparedStatement.setDate(26, endDate);
                preparedStatement.setString(27, gradeLevel);

                // Add the row to the batch
                preparedStatement.addBatch();

                // Execute the batch every batchSize records or at the end
                if ((i + 1) % batchSize == 0 || i == numRecords - 1) {
                    preparedStatement.executeBatch();
                    System.out.println("Inserted " + (i + 1) + " records");
                }
            }

            System.out.println("Successfully inserted " + numRecords + " records into the students table.");

        } catch (SQLException e) {
            System.err.println("Error inserting data: " + e.getMessage());
            e.printStackTrace(); // Good practice to print the stack trace for detailed error info
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading MySQL driver: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Helper method to generate a random date within a range
    private static java.sql.Date generateRandomDate(Random random, int startYear, int endYear) {
        int year = startYear + random.nextInt(endYear - startYear + 1);
        int month = random.nextInt(12) + 1; // 1-12
        int day = random.nextInt(31) + 1;   // 1-31 (will need to check for valid date)

        // Simple check for valid date (doesn't account for leap years)
        if (month == 2 && day > 28) {
            day = 28; //  simplification for February
        } else if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) {
            day = 30;
        }
        // Create a Calendar instance
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month - 1, day); // Month is 0-indexed in Calendar

        // Get the java.sql.Date object
        return new java.sql.Date(cal.getTimeInMillis());
    }

    // Helper method to generate a random phone number
    private static String generateRandomPhoneNumber(Random random) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10)); // Append a random digit (0-9)
        }
        return sb.toString();
    }

    // Helper method to generate a random email
    private static String generateRandomEmail(String firstName, String lastName, int index) {
        return firstName.toLowerCase() + "." + lastName.toLowerCase() + index + "@example.com";
    }

    //Helper method to generate address line 1
    private static String generateRandomAddressLine1(Random random){
        int num = random.nextInt(1000)+1;
        String[] streets = {"Main St", "Oak Ave", "Pine Ln", "Broadway", "Market St", "Grand Ave", "Elm St", "Park Ave", "Hill Rd", "Church St"};
        return num + " " + streets[random.nextInt(streets.length)];
    }

    //Helper method to generate address line 2
    private static String generateRandomAddressLine2(Random random){
        String[] aptTypes = {"Apt ", "Unit ", "Suite ", "Floor "};
        int num = random.nextInt(20)+1;
        return aptTypes[random.nextInt(aptTypes.length)] + num;
    }

    //Helper method to generate zip code
    private static String generateRandomZipCode(Random random){
        return String.format("%05d", random.nextInt(100000));
    }
}

