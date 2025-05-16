package org.example.server;


import com.google.protobuf.FieldMask;
import com.sun.management.OperatingSystemMXBean;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.example.HelloResponse;
import org.example.StudentBatchResponse;
import org.example.StudentResponse;
import org.example.StudentServiceGrpc;
import org.example.database.Database;
import org.example.model.Student;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class Server extends StudentServiceGrpc.StudentServiceImplBase{


    @Override
    public void getStudent(org.example.HelloRequest request,
                           io.grpc.stub.StreamObserver<org.example.HelloResponse> responseObserver) {
       System.out.println("Hello Student:" + request.getName());
       HelloResponse response = HelloResponse.newBuilder().setMessage("Hello Student:" + request.getName()).build();
       responseObserver.onNext(response);
       responseObserver.onCompleted();
    }

    @Override
    public void getAllStudent(org.example.StudentRequest request,
                              io.grpc.stub.StreamObserver<org.example.StudentResponse> responseObserver) {
        System.out.println("-----------------------------------------------------------------------------------------------");

        System.out.println("Getting all students");
        Student student = null;
        FieldMask fieldMask = request.getFieldMask();
        List<Long> timeTaken = new ArrayList<>();
        List<Long> memoryTaken = new ArrayList<>();
        List<Double> cpuLoad = new ArrayList<>();
        List<Integer> serialisedSizes = new ArrayList<>();

        OperatingSystemMXBean osbean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        for (int i = 0; i < 10; i++) {
            try (Connection connection = Database.getConnection();
                 ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM students")) {
                long startTime = System.currentTimeMillis();
                long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                double startCpuLoad = osbean.getProcessCpuLoad();
                while (resultSet.next()) {

                        StudentResponse studentResponse = StudentResponse.newBuilder()
                                .setStudentId(resultSet.getInt("student_id"))
                                .setFirstName(resultSet.getString("first_name"))
                                .setLastName(resultSet.getString("last_name"))
                                .setMiddleName(resultSet.getString("middle_name"))
                                .setDateOfBirth(resultSet.getString("date_of_birth"))
                                .setGender(resultSet.getString("gender"))
                                .setEmail(resultSet.getString("email"))
                                .setPhoneNumber(resultSet.getString("phone_number"))
                                .setAddressLine1(resultSet.getString("address_line1"))
                                .setAddressLine2(resultSet.getString("address_line2"))
                                .setCity(resultSet.getString("city"))
                                .setState(resultSet.getString("state"))
                                .setZipCode(resultSet.getString("zip_code"))
                                .setCountry(resultSet.getString("country"))
                                .setMajor(resultSet.getString("major"))
                                .setMinor(resultSet.getString("minor"))
                                .setGpa(resultSet.getDouble("gpa"))
                                .setCreditsCompleted(resultSet.getInt("credits_completed"))
                                .setEnrollmentStatus(resultSet.getString("enrollment_status"))
                                .setGraduationDate(resultSet.getString("graduation_date"))
                                .setAdvisorId(resultSet.getInt("advisor_id"))
                                .setFinancialAidStatus(resultSet.getString("financial_aid_status"))
                                .setScholarshipName(resultSet.getString("scholarship_name"))
                                .setRoomNumber(resultSet.getString("room_number"))
                                .setBuilding(resultSet.getString("building"))
                                .setStartDate(resultSet.getString("start_date"))
                                .setEndDate(resultSet.getString("end_date"))
                                .setGradeLevel(resultSet.getString("grade_level"))
                                .setRegistrationDate(String.valueOf(resultSet.getTimestamp("registration_date").getTime() / 1000))
                                .setLastModified(String.valueOf(resultSet.getTimestamp("last_modified").getTime() / 1000))
                                .setNotes(resultSet.getString("notes") != null ? resultSet.getString("notes") : "")
                                .build();
                        responseObserver.onNext(studentResponse);
                        serialisedSizes.add(studentResponse.getSerializedSize());

                }
                long endTime = System.currentTimeMillis();
                timeTaken.add(endTime - startTime);

                long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                memoryTaken.add(Math.abs(endMemory - startMemory));

                double endCpuLoad = osbean.getProcessCpuLoad();
                cpuLoad.add(Math.abs(endCpuLoad - startCpuLoad));


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        responseObserver.onCompleted();
        long timetaken = timeTaken.stream().mapToLong(Long::longValue).sum();
        long memorytaken = memoryTaken.stream().mapToLong(Long::longValue).sum();
        double cputaken = cpuLoad.stream().mapToDouble(Double::doubleValue).sum();
        int serialisedSize = serialisedSizes.stream().mapToInt(Integer::intValue).sum();
        System.out.println("Average Time taken to fetch all students : " + (double)(timetaken/timeTaken.size())/(1000) + "s");
        System.out.println("Average Memory taken to fetch all students : " + (memorytaken/memoryTaken.size())/(1024*1024) + "mb");
        System.out.println("Average CPU taken to fetch all students : " + String.format("%.3f", (cputaken/cpuLoad.size())) + " % ");
        System.out.println("Average Serialised Size of all students : " + String.format("%.3f", (double)(serialisedSize/serialisedSizes.size())/(1024)) + " kb");
        System.out.println("-----------------------------------------------------------------------------------------------");
    }


    @Override
    public void getBatchStudent(org.example.StudentRequest request,
                                io.grpc.stub.StreamObserver<org.example.StudentBatchResponse> responseObserver) {
        System.out.println("-----------------------------------------------------------------------------------------------");
        System.out.println("Getting all students as batches of "+request.getId()+" records each");
        List<StudentResponse> batch = new ArrayList<>();

        List<Long> timeTaken = new ArrayList<>();
        List<Long> memoryTaken = new ArrayList<>();
        List<Double> cpuLoad = new ArrayList<>();
        List<Integer> serialisedSizes = new ArrayList<>();

        OperatingSystemMXBean osbean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        for (int i = 0; i < 10; i++) {
            try (Connection connection = Database.getConnection();
                 ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM students")) {
                long startTime = System.currentTimeMillis();
                long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                double startCpuLoad = osbean.getProcessCpuLoad();
                int count = 0;
                while (resultSet.next()) {
                    batch.add(StudentResponse.newBuilder()
                            .setStudentId(resultSet.getInt("student_id"))
                            .setFirstName(resultSet.getString("first_name"))
                            .setLastName(resultSet.getString("last_name"))
                            .setMiddleName(resultSet.getString("middle_name"))
                            .setDateOfBirth(resultSet.getString("date_of_birth"))
                            .setGender(resultSet.getString("gender"))
                            .setEmail(resultSet.getString("email"))
                            .setPhoneNumber(resultSet.getString("phone_number"))
                            .setAddressLine1(resultSet.getString("address_line1"))
                            .setAddressLine2(resultSet.getString("address_line2"))
                            .setCity(resultSet.getString("city"))
                            .setState(resultSet.getString("state"))
                            .setZipCode(resultSet.getString("zip_code"))
                            .setCountry(resultSet.getString("country"))
                            .setMajor(resultSet.getString("major"))
                            .setMinor(resultSet.getString("minor"))
                            .setGpa(resultSet.getDouble("gpa"))
                            .setCreditsCompleted(resultSet.getInt("credits_completed"))
                            .setEnrollmentStatus(resultSet.getString("enrollment_status"))
                            .setGraduationDate(resultSet.getString("graduation_date"))
                            .setAdvisorId(resultSet.getInt("advisor_id"))
                            .setFinancialAidStatus(resultSet.getString("financial_aid_status"))
                            .setScholarshipName(resultSet.getString("scholarship_name"))
                            .setRoomNumber(resultSet.getString("room_number"))
                            .setBuilding(resultSet.getString("building"))
                            .setStartDate(resultSet.getString("start_date"))
                            .setEndDate(resultSet.getString("end_date"))
                            .setGradeLevel(resultSet.getString("grade_level"))
                            .setRegistrationDate(String.valueOf(resultSet.getTimestamp("registration_date").getTime() / 1000))
                            .setLastModified(String.valueOf(resultSet.getTimestamp("last_modified").getTime() / 1000))
                            .setNotes(resultSet.getString("notes") != null ? resultSet.getString("notes") : "")
                            .build());
                    count++;
                    if (count% request.getId()==0) {
                        for (StudentResponse student : batch) {
                            StudentBatchResponse studentBatchResponse = StudentBatchResponse.newBuilder().setStudentResponse(student).build();
                            responseObserver.onNext(studentBatchResponse);
                            serialisedSizes.add(studentBatchResponse.getSerializedSize());

                        }
                        batch.clear();
                    }
                }
                long endTime = System.currentTimeMillis();
                timeTaken.add(endTime - startTime);

                long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                memoryTaken.add(Math.abs(endMemory - startMemory));

                double endCpuLoad = osbean.getProcessCpuLoad();
                cpuLoad.add(Math.abs(endCpuLoad - startCpuLoad));


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        responseObserver.onCompleted();
        long timetaken = timeTaken.stream().mapToLong(Long::longValue).sum();
        long memorytaken = memoryTaken.stream().mapToLong(Long::longValue).sum();
        double cputaken = cpuLoad.stream().mapToDouble(Double::doubleValue).sum();
        int serialisedSize = serialisedSizes.stream().mapToInt(Integer::intValue).sum();
        System.out.println("********************************  Thread Name: "+Thread.currentThread().getName()+"  *******************************");
        System.out.println("Average Time taken to fetch all students as batches of "+request.getId()+" : " + (double)(timetaken/timeTaken.size())/(1000) + "s");
        System.out.println("Average Memory taken to fetch all students as batches of "+request.getId()+" : " + (memorytaken/memoryTaken.size())/(1024*1024) + "mb");
        System.out.println("Average CPU taken to fetch all students: as batches of "+request.getId()+" : " + String.format("%.3f", (cputaken/cpuLoad.size())) + " % ");
        System.out.println("Average Serialised Size of all students: as batches of "+request.getId()+" : " + String.format("%.3f", (double)(serialisedSize/serialisedSizes.size())/(1024)) + " kb");
        System.out.println("-----------------------------------------------------------------------------------------------");

    }

    public static void main(String[] args) throws IOException, InterruptedException {

//    File certFile = new File("C:\\Users\\Administrator\\IdeaProjects\\jerseygrpc\\localhost+2.pem");
//    File privateKeyFile = new File("C:\\Users\\Administrator\\IdeaProjects\\jerseygrpc\\localhost+2-key.pem");

    io.grpc.Server server = ServerBuilder.forPort(8080)
//        .useTransportSecurity(certFile, privateKeyFile)
        .addService(new Server())
        .build();
        
    server.start();
    System.out.println("Server running on Localhost:8080");
    server.awaitTermination();
}
}