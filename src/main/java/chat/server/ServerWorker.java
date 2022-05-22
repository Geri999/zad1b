package chat.server;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class ServerWorker implements Runnable {
    private  Socket socket;
    @Inject
    private CommandInterpreter commandInterpreter;

    public ServerWorker(CommandInterpreter commandInterpreter) {
        this.commandInterpreter = commandInterpreter;
        log.info("T: ServerWorker was created ");
    }
    @PostConstruct
    void postConstruct(){

        log.info("T: PostConstruct in ServerWorker with socket: {}", socket);
    }

    @Override
    public void run() {
        log.info("T: Server session started... on socket:{}", socket.toString());
        commandInterpreter.setSocket(socket);
        try {
            BufferedReader inputS = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            String message;
            while ((message = inputS.readLine()) != null) {

                log.info("T: Server received message:{}", message);
                commandInterpreter.commandInterpreter(message, output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}