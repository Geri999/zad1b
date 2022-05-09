package chat.server;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
@NoArgsConstructor
public class ServerWorker implements Runnable {
//    @Inject
//    private MessagesRepo messagesRepo;
//    @Inject
//    private RoomsRepo roomsRepo;
//    @Inject
//    private UsersRepo usersRepo;
    private  Socket socket;
    @Inject
    private CommandInterpreter commandInterpreter;

//    @Inject
    public ServerWorker(Socket socket) {
//        this.messagesRepo = messagesRepo;
//        this.roomsRepo = roomsRepo;
//        this.usersRepo = usersRepo;
        this.socket = socket;
        commandInterpreter.setSocket(socket);
    }

    @Override
    public void run() {
        log.info("GP: Server session started...");

//        ServerAPI serverAPI = ServerAPI.builder()
//                .messagesRepo(messagesRepo)
//                .roomsRepo(roomsRepo)
//                .usersRepo(usersRepo)
//                .socket(socket)
//                .build();


        try {
            BufferedReader inputS = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            String message;
            while ((message = inputS.readLine()) != null) {

                log.info("GP: Server received message:{}", message);
                commandInterpreter.commandInterpreter(message, output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}