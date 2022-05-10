package chat.server;

import chat.server.repository.RoomsRepo;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Server {
    private int port;
    private int maxClients;
    private ExecutorService executorService;
    @Inject
    private RoomsRepo roomsRepo;

    @Inject
    private CommandInterpreter commandInterpreter;

    public void start() {
        roomsRepo.createEmptyRoomAndReturnRoomId("WaitingRoom");
        startTheServer();
    }

    @Inject
    private void loadServerConfiguration(Properties properties) {
        String n = "asdad";
        port = Integer.parseInt(properties.getProperty("port"));
        maxClients = Integer.parseInt(properties.getProperty("threads"));
        executorService = Executors.newFixedThreadPool(maxClients);
        log.info("GP: Config data for SERVER loaded...");
    }

    public void startTheServer() {
        try (ServerSocket server = new ServerSocket(this.port)) {
            while (true) {
                log.info("GP: 1 - Server is listening...");
                Socket socket = server.accept();
                log.info("GP: 2 - Server accepted connection with client...Socket:{}", socket);
//                ServerWorker sw = new ServerWorker(socket);
                ServerWorker sw = new ServerWorker(socket,commandInterpreter );
                executorService.execute(sw);
                log.info("GP: 3 - New thread (session) created...");
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}