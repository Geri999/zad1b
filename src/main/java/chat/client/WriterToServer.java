package chat.client;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class WriterToServer {
    Socket socket;

    public WriterToServer(Socket socket) {
        this.socket = socket;
    }

    public static void sendToServer(String message, Client client) {
        try {
            PrintWriter output = new PrintWriter(client.getSocket().getOutputStream(), true);
            log.info("Sent to server: {}, socket: {} ", message, client.getSocket());
            output.println(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String sendToServerWithResponse(String message, Client client) {

        String answer = "";
        Socket socket = client.getSocket();
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            log.info("Sent to server message: {}, socket: {} ", message, socket);
            output.println(message);

            while (answer.isBlank()) {
                answer = input.readLine();
            }
            log.info("Received from server: {}", answer);

        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return answer;
    }
}
