package server;

import protocol.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final String storageDir;
    private final AtomicInteger activeClients;

    private MessageReader reader;
    private MessageWriter writer;

    // After login, it gets populated, null = not authenticated
    private String authenticatedUser = null;

    public ClientHandler(Socket socket, String storageDir, AtomicInteger activeClients) {
        this.socket = socket;
        this.storageDir = storageDir;
        this.activeClients = activeClients;
    }

    @Override
    public void run() {
        String clientAddress = socket.getInetAddress().getHostAddress();
        try {
            reader = new MessageReader(socket.getInputStream());
            writer = new MessageWriter(socket.getOutputStream());
            log("Opened session with " + clientAddress);
            handleSession();
        } catch (IOException e) {
            log("[WARN] Connection lost from " + clientAddress + ": " + e.getMessage());
        } finally {
            closeQuietly();
            activeClients.decrementAndGet();
            log("Closed session with " + clientAddress + " - active clients: " + activeClients.get());
        }
    }

    // Main session loop
    private void handleSession() throws IOException {
        while (!socket.isClosed()) {

            Message msg = reader.read();

            // Before login, accept only CMD_LOGIN
            if (authenticatedUser==null && msg.getCmd()!=Protocol.CMD_LOGIN) {
                writer.error("You have to login first");
                continue;
            }

            switch (msg.getCmd()) {
                case Protocol.CMD_LOGIN -> doNothing();


                default -> writer.error("Unkwnown command: " + msg.getCmd());
            }

        }
    }


    private void doNothing() {
        return;
    }

}
