package chat.server;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
@Data
public class ServerWorker implements Runnable {
    private  Socket socket;
//    @Inject
    private CommandInterpreter commandInterpreter;

    public ServerWorker(Socket socket, CommandInterpreter commandInterpreter) {
        this.socket = socket;
        this.commandInterpreter = commandInterpreter;
        log.info("GP: ServerWorker created ");
    }

    @Override
    public void run() {
        log.info("GP: Server session started... on socket:{}", socket.toString());
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