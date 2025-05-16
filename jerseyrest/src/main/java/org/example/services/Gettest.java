package org.example.services;

import com.sun.management.OperatingSystemMXBean;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.lang.management.ManagementFactory;
import java.sql.*;

// Single payload all data
@Path("getall")
public class Gettest {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getAll() throws SQLException {
        System.out.println("Getting all data as single payload...");
        long starttime = System.currentTimeMillis();
        int serialisedSize = 0;
        long startMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024);
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double startCpuLoad = osBean.getProcessCpuLoad();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement("select * from students");
             ResultSet resultSet = statement.executeQuery()) {

            StringBuilder data = new StringBuilder();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // First add headers
            for (int i = 1; i <= columnCount; i++) {
                data.append(metaData.getColumnName(i));
                if (i < columnCount) data.append(",");
            }
            data.append("\n");

            // Then add data
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    data.append(resultSet.getString(i));
                    if (i < columnCount) data.append(",");
                }
                data.append("\n");
            }
            serialisedSize = data.toString().getBytes().length;
            return data.toString();
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
