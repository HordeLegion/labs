import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket("localhost", 5678);
            System.out.println("Connected to server");

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

            Thread serverThread = new Thread(new ServerHandler(reader));
            serverThread.start();

            Scanner scanner = new Scanner(System.in);
            String message;
            while (true) {
                message = scanner.nextLine();
                if (message.startsWith("@name")) {
                    writer.println(message);
                } else if (message.startsWith("@senduser")) {
                    writer.println(message);
                } else if (message.equals("@quit")) {
                    writer.println(message);
                    break;
                } else {
                    writer.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ServerHandler implements Runnable {
    private final BufferedReader reader;

    public ServerHandler(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}