package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class client {
    private static final int TCP_PORT = 23;

    public static void main(String[] args) throws IOException {
        Socket socket;
        BufferedReader keybReader = new BufferedReader(new InputStreamReader(System.in));

        // Establishing connection
        while (true) {
            System.out.print("Enter Server name/address (default - localhost): ");
            try {
                String serverName = keybReader.readLine();
                if (serverName.isEmpty()) serverName = "localhost";
                InetAddress address = InetAddress.getByName(serverName);
                System.out.println(address);
                socket = new Socket(address, TCP_PORT);

                if (socket.isConnected()) break;

            } catch (UnknownHostException e) {
                System.out.println("Unknown server, try again\n");
            } catch (IOException e) {
                System.out.println(e.toString());
                System.out.println("Incorrect address or server is not started, try again\n");
            }

        }

        // Working
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()), true);

        System.out.println(in.readLine());

        System.out.println("Enter command or EXIT");
        // asynchronous send commands and receive responses
        while (true) {
            if (keybReader.ready()) {
                String command = keybReader.readLine();
                out.println(command);
                if (command.equals("exit")) {
                    socket.close();
                    break;
                }
            }

            while (in.ready()) System.out.println(in.readLine());
        }


    }
}
