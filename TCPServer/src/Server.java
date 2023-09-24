import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final Set<PrintWriter> clients = new HashSet<>();
    private static final Map<String, PrintWriter> clientNames = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5678);
            System.out.println("Server started");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(clientSocket);
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clients.add(writer);
                Thread clientThread = new Thread(new ClientHandler(clientSocket, writer));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMessage(String message, PrintWriter writer) {
        for (PrintWriter client : clients) {
            if(client != writer)
                client.println(message);
        }
    }

    public static void sendMessageToUser(String sender, String receiver, String message) {
        if (clientNames.containsKey(receiver)) {
            PrintWriter receiverWriter = clientNames.get(receiver);
            receiverWriter.println(">>>" + sender + "<<<" + ": " + message);
        }
    }

    public static void setClientName(String client, PrintWriter writer) {
        clientNames.put(client, writer);
    }

    public static void removeClientName(String client) {
        clientNames.remove(client);
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final PrintWriter writer;
    private String clientName = "Unknown";

    public ClientHandler(Socket clientSocket, PrintWriter writer) {
        this.clientSocket = clientSocket;
        this.writer = writer;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String message;
            while ((message = reader.readLine()) != null) {
                if (message.startsWith("@name")) {
                    setClientName(message.substring(6), writer);
                } else if (message.startsWith("@senduser")) {
                    String[] parts = message.split(" ", 3);
                    String receiver = parts[1];
                    String msg = parts[2];
                    Server.sendMessageToUser(clientName, receiver, msg);
                } else if (message.equals("@quit")) {
                    Server.removeClientName(clientName);
                    break;
                } else {
                    Server.broadcastMessage(clientName + ": " + message, writer);
                }
            }

            reader.close();
            writer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setClientName(String name, PrintWriter writer) {
        clientName = name;
        Server.setClientName(clientName, writer);
    }
}