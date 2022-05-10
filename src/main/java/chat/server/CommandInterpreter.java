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

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
//@Default
public class CommandInterpreter {
//    @Inject
    private MessagesTxtRepo messagesTxtRepo;
//    @Inject
    private RoomsRepo roomsRepo;
//    @Inject
    private UsersRepo usersRepo;
    private Socket socket;


    public CommandInterpreter(MessagesTxtRepo messagesTxtRepo, RoomsRepo roomsRepo, UsersRepo usersRepo) {
        log.info("GP: CommandInterpreter created...");
        this.messagesTxtRepo = messagesTxtRepo;
        this.roomsRepo = roomsRepo;
        this.usersRepo = usersRepo;
    }

    public CommandInterpreter() {
        log.info("GP: CommandInterpreter created...");
    }

    /*    public ServerAPI(Socket socket) {
        this.socket = socket;
    }*/

    public void commandInterpreter(String message, PrintWriter output) {
        String command = message.split("\\|")[0];
        log.info("GP: command: {}",command);
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
                roomCreationRequest(message, output);
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
        log.info("GP: User logged and added to Waiting Room");
    }


    private void currentUserListRequest(PrintWriter output) {
        String collect = usersRepo.findAllCurrentUsers().stream().map(User::getUserName).collect(Collectors.joining("|"));
        output.println(collect);
        log.info("GP: User list prepared and sent to Client");
    }


    private void logoutRequest(String message, PrintWriter output) {
        User user = usersRepo.findUserByName(message.split("\\|")[1]);
        usersRepo.removeUserFromRoom(user.getUserName());

        output.println(true);
        //todo zamknij pokój gdy było ich tylko 2, odejmij z pokojów wielosobowych, odejmij z listy klientów
        log.info("GP: (Logout) User removed from usersRepo");
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


    private void roomCreationRequest(String message, PrintWriter output) {
        String usersInvitedToChat = message.split("\\|")[3];
        List<String> usersInvitedToChatList = stringToListParser(usersInvitedToChat);

        Room room = new Room();

        for (String userName : usersInvitedToChatList) {
            User userByName = usersRepo.findUserByName(userName);
            room.getUsersInRoom().add(userByName);
        }

        long roomId = roomsRepo.createRoomWithUsers(room);
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
        List<User> usersSet = roomsRepo.findAllUsersInTheRoom(roomId);
        HashSet<User> users = new HashSet<>(usersSet);
        roomById.broadcastToAllRoomParticipant(message);
        IOTools.saveMessageToFile(message, roomById);
        messagesTxtRepo.save(new MessageTxt(users   , text));
        log.info("GP: Broadcasting text={}", message);
        log.info("GP: text saved to hdd and to DB: text:{}, roomId:{}", text, roomById);
    }












}