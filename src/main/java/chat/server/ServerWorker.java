package chat.server;

import chat.server.repository.MessagesRepo;
import chat.server.repository.RoomsRepo;
import chat.server.repository.UsersRepo;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class ServerWorker implements Runnable {
    private final MessagesRepo messagesRepo;
    private final RoomsRepo roomsRepo;
    private final UsersRepo usersRepo;
    private final Socket socket;

    public ServerWorker(MessagesRepo messagesRepo, RoomsRepo roomsRepo, UsersRepo usersRepo, Socket socket) {
        this.messagesRepo = messagesRepo;
        this.roomsRepo = roomsRepo;
        this.usersRepo = usersRepo;
        this.socket = socket;
    }

    @Override
    public void run() {
        log.info("Session started...");

        ServerAPI serverAPI = ServerAPI.builder()
                .messagesRepo(messagesRepo)
                .roomsRepo(roomsRepo)
                .usersRepo(usersRepo)
                .socket(socket)
                .build();

        try {
            BufferedReader inputS = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            String message;
            while ((message = inputS.readLine()) != null) {

                log.info("Server received message:{}", message);
                serverAPI.commandInterpreter(message, output);
            }
            log.info("poza while");
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Wskoczyłem do wyjatkowa");
        }
    }
}