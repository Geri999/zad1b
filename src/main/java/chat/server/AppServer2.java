package chat.server;

import chat.commons.IOTools;
import chat.server.repository.MessagesRepo;
import chat.server.repository.Room2Repo;
import chat.server.repository.RoomsRepo;
import chat.server.repository.UsersRepo;
import lombok.extern.slf4j.Slf4j;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class AppServer2 {
    private int port;
    private int maxClients;
    private ExecutorService executorService;

    //    @Inject
    private MessagesRepo messagesRepo;
//    @Inject
    private RoomsRepo roomsRepo;
//    @Inject
    private UsersRepo usersRepo;
//    @Inject
    private Room2Repo room2Repo;

    public static void main(String[] args) {
        log.info("Application started...");
        Weld weld = new Weld();
//        weld.enableDiscovery()
//                .addPackage(true, AppServer2.class);
        WeldContainer container = weld.initialize();
        new AppServer2();

        container.close();
    }

    public AppServer2() {
//        this.messagesRepo = new MessagesRepo();
//        this.roomsRepo = new RoomsRepo();
//        this.usersRepo = new UsersRepo();


        EntityManagerFactory emf = Persistence.createEntityManagerFactory("defaultH2");
        log.info("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        room2Repo.createEmptyRoomAndReturnRoomId("WaitingRoom");

/*
                EntityManager entityManager = emf.createEntityManager();
        Room2 adminRoom = new Room2();
        User2 admin = new User2("Admin", adminRoom);

        entityManager.getTransaction().begin();
        entityManager.persist(admin);
        entityManager.getTransaction().commit();
        entityManager.close();*/

        loadServerConfiguration();
        startTheServer();
        emf.close();

    }

    private void loadServerConfiguration() {
        HashMap<String, String> configMap = IOTools.loadConfigFile();
        port = Integer.parseInt(configMap.get("port"));
        maxClients = Integer.parseInt(configMap.get("threads"));
        executorService = Executors.newFixedThreadPool(maxClients);
        log.info("Config data for SERVER loaded...");
    }

    public void startTheServer() {
        try (ServerSocket server = new ServerSocket(this.port)) {
            while (true) {
                log.info("1 - Server is listening...");
                Socket socket = server.accept();
                log.info("2 - Server accepted connection with client...Socket:{}", socket);
                ServerWorker sw = new ServerWorker(messagesRepo, roomsRepo, usersRepo, socket);
                executorService.execute(sw);
                log.info("3 - New thread (session) created...");
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}