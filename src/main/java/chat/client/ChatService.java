package chat.client;


import chat.commons.Commands;
import chat.commons.IOTools;
import chat.commons.MessageMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
public class ChatService {
    private final Client client;

    public ChatService(Client client) {
        this.client = client;
    }

    public void joinToChat() {
        log.info("ChatService joinToChat() method started");

        String roomId = findRoomIdByClient(client);
        if ("empty".equals(roomId)) {
            System.out.println("You are not in any room right now");
            return;
        }
        readingThreadDemonCreation();
        chatConversation(roomId);
    }

    public void createAndBeginChat() {
        log.info("ChatService chat() method started");

        String roomId = chatRoomCreation();
        if ("menu".equals(roomId)) return;

        readingThreadDemonCreation();
        chatConversation(roomId);

    }

    private String findRoomIdByClient(Client client) {
        String findRoomIdByUserNameMessage = MessageMapper.createFindRoomIdByUserNameMessage(Commands.$FIND_ROOM_ID_BY_USERNAME_MSG, client.getClientName());
        String roomId = WriterToServer.sendToServerWithResponse(findRoomIdByUserNameMessage, client);
        log.info("GP: ID room by UserName={}", roomId);
        return "empty".equals(roomId) ? "empty" : roomId;
    }

    private String chatRoomCreation() {
        Scanner sc = new Scanner(System.in);
        List<String> usersInvitedToChat = new ArrayList<>();
        usersInvitedToChat.add(client.getClientName());

        List<String> allUsersOnline = ClientCommands.getUserListCommand(client);

        String inputUserName = "";

        System.out.println(allUsersOnline.stream().collect(Collectors.joining(", ", "Users on-Line: ", ".")));
        System.out.println("\n*** ADDING USERS TO ROOM ***");
        System.out.println("- type \"end\" to finish adding users and start chat");
        System.out.println("- type \"menu\" to return to main menu");

        while (true) {
            System.out.print("Type the username you want to add to the room: " + client.getPrompt());

            inputUserName = sc.nextLine();
            if (inputUserName.equals("menu")) return "menu";
            if (inputUserName.equalsIgnoreCase("end")) break;

            boolean isUserOnLine = allUsersOnline.contains(inputUserName);
            if (isUserOnLine) usersInvitedToChat.add(inputUserName);
            System.out.println(isUserOnLine
                    ? String.format("Ok, %s is added to room.", inputUserName)
                    : String.format("Sorry, %s is NOT on-line.", inputUserName));
        }

//        String requestForRoomMessage = MessageMapper.createRequestForRoomMessage(Commands.$CREATE_ROOM_REQUEST, usersInvitedToChat);
        String collect = usersInvitedToChat.stream().collect(Collectors.joining("#"));
        String requestForRoomMessage = MessageMapper.messageConverter(Commands.$CREATE_ROOM_REQUEST, client.getClientName(), "newRoom", collect);
        String roomId = WriterToServer.sendToServerWithResponse(requestForRoomMessage, client);

        return roomId;
    }

    private void readingThreadDemonCreation() {
        log.info("New thread for reading from server and print on console");
        Thread thread = new Thread(() -> new ReaderFromServer(client).readFromServerAndPrintOnConsole());
        thread.setDaemon(true);
        thread.start();
    }

    private void chatConversation(String roomId) {
        System.out.println("\n" + "*".repeat(20) + "CHAT STARTED:" + "*".repeat(20));
        System.out.println("(type @END to stop conversation)");
        System.out.println("(type @SEND to enter file sending menu)");
        System.out.println("Ask your other chat participants to enter the room (menu item 3)");

        String text;
        Scanner sc = new Scanner(System.in);
        boolean loopCondition = true;
        String message;
        while (loopCondition) {
            switch (text = sc.nextLine()) {
                case "@end":
                case "@END":
                    loopCondition = false;
                    text = client.getClientName()+ "leaved the room.";
                    message = MessageMapper.messageConverter(Commands.$LEAVING_THE_ROOM_REQUEST, client.getClientName(), roomId, text);
                    WriterToServer.sendToServer(message, client);
                    break;
                case "@send":
                case "@SEND":
                    message = MessageMapper.messageConverter(Commands.$SEND_FILE_MSG, client.getClientName(), roomId, text);
                    WriterToServer.sendToServer(message, client);
                    IOTools.sendFile(client.getSocket());
                    log.info("End of @SEND command");
                    break;
                default:
                    message = MessageMapper.messageConverter(Commands.$BROADCAST_TEXT_MSG, client.getClientName(), roomId, text);
                    WriterToServer.sendToServer(message, client);
                    log.info(Commands.$BROADCAST_TEXT_MSG.toString());
                    log.info(client.getClientName());
                    log.info(roomId);
                    log.info(text);
            }
        }
        System.out.println("**** End of conversation ****");
    }
}