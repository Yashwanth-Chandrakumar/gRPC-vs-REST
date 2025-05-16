package org.example.services;

import com.sun.management.OperatingSystemMXBean;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.example.model.Student;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Path( "/getjson")
public class Getjson {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Student> getjson() {
        System.out.println("Getting all data as single json payload...");
        long starttime = System.currentTimeMillis();
        int serialisedSize = 0;
        long startMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024);
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double startCpuLoad = osBean.getProcessCpuLoad();

        List<Student> students = new ArrayList<>();
        try(Connection connection = Database.getConnection();
            PreparedStatement statement = connection.prepareStatement("select * from students");
            ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()){
                students.add(new Student(
                        resultSet.getInt("student_id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("middle_name"),
                        resultSet.getDate("date_of_birth"),
                        resultSet.getString("gender"),
                        resultSet.getString("email"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("address_line1"),
                        resultSet.getString("address_line2"),
                        resultSet.getString("city"),
                        resultSet.getString("state"),
                        resultSet.getString("zip_code"),
                        resultSet.getString("country"),
                        resultSet.getString("major"),
                        resultSet.getString("minor"),
                        resultSet.getDouble("gpa"),
                        resultSet.getInt("credits_completed"),
                        resultSet.getString("enrollment_status"),
                        resultSet.getDate("graduation_date"),
                        resultSet.getInt("advisor_id"),
                        resultSet.getString("financial_aid_status"),
                        resultSet.getString("scholarship_name"),
                        resultSet.getString("room_number"),
                        resultSet.getString("building"),
                        resultSet.getDate("start_date"),
                        resultSet.getDate("end_date"),
                        resultSet.getString("grade_level"),
                        resultSet.getTimestamp("registration_date"),
                        resultSet.getTimestamp("last_modified"),
                        resultSet.getString("notes")
                ));
            }
            serialisedSize = students.toString().getBytes().length;
            return students;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            long endtime = System.currentTimeMillis();
            long endMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024);
            double endCpuLoad = osBean.getProcessCpuLoad();
            System.out.println("Time taken: " + (double)(endtime - starttime)/1000 + " s");
            System.out.println("Serialised data: " +serialisedSize+ " bytes" + " ( "+(serialisedSize/1024)+" KB )");
            System.out.println("Memory used: " + Math.abs(endMemory-startMemory) + " MB");
            System.out.printf("CPU load: %.3f %%%n", Math.abs(endCpuLoad-startCpuLoad));
        }
    }
}