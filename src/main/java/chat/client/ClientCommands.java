package chat.client;

import chat.commons.Commands;
import chat.commons.IOTools;
import chat.commons.MessageMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
@Data
public class ClientCommands {

    public static void loginCommand(Client client) {
        if (client.isLogged()) {
            System.out.println("You are logged");
            return;
        }
        int counter = 0;
        while (counter < 3) {
            System.out.printf("What's your name(Login)? %s", client.getPrompt());
            String clientName = new Scanner(System.in).nextLine();
            String loginMessage = MessageMapper.createLoginMessage(Commands.$LOGIN_REQUEST, clientName, client.getSocket());
            boolean result = Boolean.parseBoolean(WriterToServer.sendToServerWithResponse(loginMessage, client));

            if (result) {
                client.setClientName(clientName);
                client.setLogged(true);
                return;
            } else {
                System.out.println("You can't login because you're on black list or you enter wrong password!");//todo password
                counter++;
            }
        }
        System.out.println("You can't login. Good bye.");
        try {
            client.getSocket().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.exit(0);
    }

    public static void createChatCommand(Client client) {
        log.info("createChatCommand(Client client)");
        Thread thread = new Thread(() -> new ChatService(client).createAndBeginChat());
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void joinToChatCommand(Client client) {
        log.info("joinToChatCommand(Client client)");
        Thread thread = new Thread(() -> new ChatService(client).joinToChat());
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void printAllMyChatsCommand(Client client) {
        IOTools.loadMessageFromFile(client.getClientName()).forEach(System.out::println);
    }


    public static List<String> getUserListCommand(Client client) {
        String allUsersListMessage = MessageMapper.createAllUsersListMessage(Commands.$USERS_LIST_REQUEST);
        String serverResponse = WriterToServer.sendToServerWithResponse(allUsersListMessage, client);
        List<String> usersList = Arrays.asList(serverResponse.split("\\|"));
        return usersList;
    }

    public static void printUserListCommand(Client client) {
        String list = getUserListCommand(client)
                .stream()
                .collect(Collectors.joining(", ", "On-Line users: ", "."));
        System.out.println(list);
    }

    public static boolean logoutCommand(Client client) {
        if (!client.isLogged()) {
            System.out.println("You are NOT logged");
            return false;
        }
        String exitMessage = MessageMapper.createExitMessage(Commands.$LOGOUT_REQUEST, client.getClientName());
        return Boolean.parseBoolean(WriterToServer.sendToServerWithResponse(exitMessage, client));
    }
}