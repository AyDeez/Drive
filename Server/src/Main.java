import server.ConnectionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {

        // Loads configuration from external file
        Properties config = loadConfig("server.properties");
        int port = Integer.parseInt(config.getProperty("port", "9090"));
        String storageDir = config.getProperty("storage.dir", "./storage");
        int maxClients = Integer.parseInt(config.getProperty("max.clients", "4"));

        System.out.println("=== Java Drive Server ===");
        System.out.println("Port:          " + port);
        System.out.println("Storage:       " + storageDir);
        System.out.println("Max Client:    " + maxClients);

        try {
            ConnectionListener listener = new ConnectionListener(port, maxClients, storageDir);
            listener.start();
        } catch (IOException e) {
            System.err.println("[FATAL] Error while starting server: " + e.getMessage());
            System.exit(1);
        }




    }

    private static Properties loadConfig(String path) {
        Properties properties = new Properties();
        File file = new File(path);

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
                System.out.println("Configuration load successfully from: " + path);
            } catch (IOException e) {
                System.err.println("[WARN] Error while reading config file, using default values");
            }
        } else {
            System.out.println("[WARN] " + path + " not found, using default values");
        }

        return properties;
    }

}
