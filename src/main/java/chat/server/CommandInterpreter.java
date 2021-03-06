package chat.server;

import chat.commons.IOTools;
import chat.commons.entities.MessageTxt;
import chat.commons.entities.Room;
import chat.commons.entities.User;
import chat.server.repository.MessagesTxtRepo;
import chat.server.repository.RoomsRepo;
import chat.server.repository.UsersRepo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Data
public class CommandInterpreter {
    private MessagesTxtRepo messagesTxtRepo;
    private RoomsRepo roomsRepo;
    private UsersRepo usersRepo;
    private Socket socket;

    @Inject
    public CommandInterpreter(MessagesTxtRepo messagesTxtRepo, RoomsRepo roomsRepo, UsersRepo usersRepo) {
        log.info("GP: CommandInterpreter was created...");
        this.messagesTxtRepo = messagesTxtRepo;
        this.roomsRepo = roomsRepo;
        this.usersRepo = usersRepo;
    }

    public void commandInterpreter(String message, PrintWriter output) {
        String command = message.split("\\|")[0];
        log.info("GP: command: {}", command);
        switch (command) {
            case "$LOGIN_REQUEST":
                loginRequest(message, output);
                break;
            case "$USERS_LIST_REQUEST":
                currentUserListRequest(output);
                break;
            case "$LOGOUT_REQUEST":
                logoutRequest(message, output);
                break;
            case "$FIND_ROOM_ID_BY_USERNAME_MSG":
                oneRoomIdByUserNameRequest(message, output);
                break;
            case "$CREATE_ROOM_REQUEST":
                newRoomWithUsersCreationRequest(message, output);
                break;
            case "$BROADCAST_TEXT_MSG":
                receiveAndBroadcastChatText(message);
                break;
            case "$SEND_FILE_MSG":
                IOTools.receiveAndSaveFile(socket, usersRepo);
                break;
            case "$LEAVING_THE_ROOM_REQUEST":
                receiveAndBroadcastChatText(message);
                //todo
                usersRepo.removeUserFromRoom(message);
                break;
        }
    }

    private void loginRequest(String message, PrintWriter output) {
        String[] split = message.split("\\|");
        String userName = split[1];
        usersRepo.addUser(new User(userName, socket));
        output.println("true");
        log.info("GP: User was logged and added to Waiting Room");
    }


    private void currentUserListRequest(PrintWriter output) {
        String collect = usersRepo.findAllCurrentUsers().stream().map(User::getUserName).collect(Collectors.joining("|"));
        output.println(collect);
        log.info("GP: User list was prepared and sent to Client");
    }


    private void logoutRequest(String message, PrintWriter output) {
        User user = usersRepo.findUserByName(message.split("\\|")[1]);
        usersRepo.removeUserFromRoom(user.getUserName());

        output.println(true);
        //todo zamknij pok??j gdy by??o ich tylko 2, odejmij z pokoj??w wielosobowych, odejmij z listy klient??w
        log.info("GP: (Logout) User was removed from usersRepo");
    }

    private void oneRoomIdByUserNameRequest(String message, PrintWriter output) {
        //find one room only !!!!!!!!!!!!!//todo
        User user = usersRepo.findUserByName(message.split("\\|")[1]);
        List<Room> rooms = roomsRepo.findAllRoomsByUserNameWhereIsUser(user.getUserName());
        String roomId;
        if (rooms.size() < 1) {
            roomId = "empty";
        } else {
            roomId = rooms.stream().findFirst().get().getRoomId().toString();
        }
        output.println(roomId);
    }


    private void newRoomWithUsersCreationRequest(String message, PrintWriter output) {
        String usersInvitedToChat = message.split("\\|")[3];
        List<String> usersNameInvitedToChatList = stringToListParser(usersInvitedToChat);
        List<User> userList = usersNameInvitedToChatList.stream().map(u -> usersRepo.findUserByName(u)).collect(Collectors.toList());

        long roomId = roomsRepo.createRoomAndMoveUsersToThatRoom(usersNameInvitedToChatList);
        output.println(roomId);
    }

    public static List<String> stringToListParser(String input) {
        return Arrays.stream(input.split("#")).collect(Collectors.toList());
    }

    private void receiveAndBroadcastChatText(String message) {
        String[] splitChat = message.split("\\|");
        Long roomId = Long.parseLong(splitChat[2]);
        String text = splitChat[3];

        log.info("GP: case=$BROADCAST_TEXT_MSG, room id={}", roomId);

        Room roomById = roomsRepo.findRoomById(roomId);
        Set<User> users = roomsRepo.findAllUsersInTheRoomByRoomId(roomId);
        for (User user : users) {
            Socket s = usersRepo.getUserSocket().get(user.getUserName());
            try {
                log.info("T: Wysy??am: {}    {}     {}", user,s, message);
                new PrintWriter(s.getOutputStream(), true).println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

//        broadcastToAllRoomParticipant(message, roomById);
        IOTools.saveMessageToFile(message, roomById);
        messagesTxtRepo.save(new MessageTxt(users, text));
        log.info("GP: Broadcasting text={}", message);
        log.info("GP: Text was saved to hdd and to DB: text:{}, roomId:{}", text, roomById);
    }


/*    public void broadcastToAllRoomParticipant(String chatMessage, Room roomById) {
        try {
            for (User user : roomById.getUsersInRoom()) {
                Socket socket = user.getSocket();
                new PrintWriter(socket.getOutputStream(), true).println(chatMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


}