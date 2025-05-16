package org.example.services;

import com.google.gson.Gson;
import com.sun.management.OperatingSystemMXBean;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.StreamingOutput;
import org.example.model.Student;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Path( "/getbatch")
public class Getbatch {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StreamingOutput getBatch(@QueryParam("batch_id") int batchSize) {
        return output -> {
            System.out.println("Getting batch of sizes: " + batchSize + " each ");
            List<Long> timetakens = new ArrayList<>();
            List<Integer> serialisedSizes = new ArrayList<>();
            List<Long> memorySizes = new ArrayList<>();
            List<Double> cpuLoads = new ArrayList<>();
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            List<Student> batch = new ArrayList<>();
            Writer writer = new OutputStreamWriter(output);
            int count = 0;
            long timetaken = 0;
            long memoryused = 0;
            double cpuLoad = 0.0;
            for (int i=0;i<10;i++) {
                try (
                        Connection connection = Database.getConnection();
                        PreparedStatement statement = connection.prepareStatement("select * from students");
                        ResultSet resultSet = statement.executeQuery();

                ) {
                    long startTime = System.currentTimeMillis();
                    long startMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024*1024);
                    double startCpuLoad = osBean.getProcessCpuLoad();
                    while (resultSet.next()) {
                        batch.add(new Student(
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
                        count++;
                        if (count % batchSize == 0) {
                            Gson gson = new Gson();
                            String json = gson.toJson(batch);
                            serialisedSizes.add(json.getBytes().length);
                            writer.write(json);
                            writer.write("\n");
                            writer.flush();
                            batch.clear();
//                        System.out.println("Batch size: " + batchSize + " Count: " + count);
                        }
                    }
                    long endTime = System.currentTimeMillis();
                    timetaken = endTime - startTime;

                    long endMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/(1024*1024);
                    memoryused = Math.abs(endMemory-startMemory);

                    double endCpuLoad = osBean.getProcessCpuLoad();
                    cpuLoad = Math.abs(endCpuLoad-startCpuLoad);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    timetakens.add(timetaken);
                    memorySizes.add(memoryused);
                    cpuLoads.add(cpuLoad);
//                    System.out.println(timetakens.size()+") Time taken: " + timetaken + " ms");
                }
            }
            long averageTime = 0;
            for(long i:timetakens){
                averageTime+=i;
            }
            averageTime/=timetakens.size();

            long averageMemory = 0;
            for(long i:memorySizes){
                averageMemory+=i;
            }
            averageMemory/=memorySizes.size();

            double averageCpuLoad = 0.0;
            for(double i:cpuLoads){
                averageCpuLoad+=i;
            }
            averageCpuLoad/=cpuLoads.size();

            int averageSerialisedSize = 0;
            for(int i:serialisedSizes){
                averageSerialisedSize+=i;
            }
            averageSerialisedSize/=serialisedSizes.size();
            System.out.println("----------------------------------------------------------------------------");
            System.out.println("Thread Name: "+Thread.currentThread().getName()+ " - Average time taken: " + (double)averageTime/1000 + " s");
            System.out.println("Thread Name: "+Thread.currentThread().getName()+ " - Average memory used: " + averageMemory + " MB");
            System.out.printf("Thread Name: %s - Average CPU load: %.3f %%%n", Thread.currentThread().getName(), averageCpuLoad);
            System.out.println("Thread Name: "+Thread.currentThread().getName()+ " - Average serialised size: " + averageSerialisedSize + " bytes" + " ( "+(averageSerialisedSize/1024)+" KB )");
            System.out.println("----------------------------------------------------------------------------");
        };
    }
}
