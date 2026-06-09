package server;

import protocol.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionListener {

    private final int port;
    private final int maxClients;
    private final String storageDir;
    private final AtomicInteger activeClients = new AtomicInteger(0);

    public ConnectionListener(int port, int maxClients, String storageDir) {
        this.port = port;
        this.maxClients = maxClients;
        this.storageDir = storageDir;
    }

    public void start() throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            log("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientAddress = clientSocket.getInetAddress().getHostAddress();

                if (activeClients.get() >= maxClients) {
                    log("[REFUSED] " + clientAddress + " - server full (" + maxClients + "/" + maxClients + ")");
                    rejectClient(clientSocket);
                    continue;
                }

                activeClients.incrementAndGet();
                log("[+] Connection from: " + clientAddress + " - active clients: " + activeClients.get() + "/" + maxClients);

                // Start new thread for this client
                Thread thread = new Thread(new ClientHandler(clientSocket, storageDir, activeClients));
                thread.setName("client-" + clientAddress);
                thread.setDaemon(true);
                thread.start();
            }
        }


    }

    // Informs the client that server is full, then closes
    private void rejectClient(Socket socket) {
        try (socket) {
            new protocol.MessageWriter(socket.getOutputStream()).error("Server is full, try again later");
        } catch (IOException ignored) {}
    }

    private static void log(String msg) {
        System.out.printf("[%s] %s%n", new java.util.Date().toString(), msg);
    }

}
