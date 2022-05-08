package chat.server;

import chat.commons.IOTools;
import chat.commons.entities.Room;
import chat.commons.entities.User;
import chat.server.repository.MessagesRepo;
import chat.server.repository.RoomsRepo;
import chat.server.repository.UsersRepo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
@NoArgsConstructor
public class CommandInterpreter {
//    @Inject
    private MessagesRepo messagesRepo;
//    @Inject
    private RoomsRepo roomsRepo;
//    @Inject
    private UsersRepo usersRepo;
    private Socket socket;

    @Inject
    public CommandInterpreter(MessagesRepo messagesRepo, RoomsRepo roomsRepo, UsersRepo usersRepo) {
        this.messagesRepo = messagesRepo;
        this.roomsRepo = roomsRepo;
        this.usersRepo = usersRepo;
    }

    /*    public ServerAPI(Socket socket) {
        this.socket = socket;
    }*/

    public void commandInterpreter(String message, PrintWriter output) {
        String command = message.split("\\|")[0];
        log.info(command);
        switch (command) {
            case "$LOGIN_REQUEST":
                loginUserAndReturnInfoToClient(message, output);
                break;
            case "$USERS_LIST_REQUEST":
                sendCurrentUserListToServer(output);
                break;
            case "$LOGOUT_REQUEST":
                logoutUserRequest(message, output);
                break;
            case "$FIND_ROOM_ID_BY_USERNAME_MSG":
                findAndReturnToClientRoomId(message, output);
                break;
            case "$CREATE_ROOM_REQUEST":
                createRoomAndReturnInfoToClient(message, output);
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


    private void loginUserAndReturnInfoToClient(String message, PrintWriter output) {
        String[] split = message.split("\\|");
        String userName = split[1];
        usersRepo.addUser(new User(userName, socket));
        output.println("true");
        log.info("GP: User logged and added to Waiting Room");
    }


    private void sendCurrentUserListToServer(PrintWriter output) {
        String collect = usersRepo.findAllCurrentUsers().stream().map(User::getUserName).collect(Collectors.joining("|"));
        output.println(collect);
        log.info("GP: User list prepared and sent to Client");
    }


    private void logoutUserRequest(String message, PrintWriter output) {
        User user = usersRepo.findUserByName(message.split("\\|")[1]);
        usersRepo.removeUserFromRoom(user.getUserName());

        output.println(true);
        //todo zamknij pokój gdy było ich tylko 2, odejmij z pokojów wielosobowych, odejmij z listy klientów
        log.info("GP: (Logout) User removed from usersRepo");
    }







    private void receiveAndBroadcastChatText(String message) {
        String[] splitChat = message.split("\\|");
        Long roomId = Long.parseLong(splitChat[2]);
        String text = splitChat[3];

        log.info("case=$BROADCAST_TEXT_MSG, room id={}", roomId);

        Room roomById = roomsRepo.findRoomById(roomId);

        roomById.broadcastToAllRoomParticipant(message);
        IOTools.saveMessageToFile(message, roomById);
        log.info("broadcasting text={}", message);
    }

    private void createRoomAndReturnInfoToClient(String message, PrintWriter output) {
        String s = message.split("\\|")[1];
        List<String> userNameForRoomList = stringToListParser(s);

        Room room = new Room();

        for (String userN : userNameForRoomList) {
            User userByName1 = usersRepo.findUserByName(userN);
            room.getUserListInRoom().add(userByName1);
        }

        //todo: sprawdz czy już taki pokój istnieje, jesli nie to nie ma co dodawać, tylko zwróc jego ID
        roomsRepo.getRoomsList().add(room);
        output.println(room.getRoomId());
    }

    //todo przenieść do roomRepo
    private void findAndReturnToClientRoomId(String message, PrintWriter output) {
        //find one room only
        User user = usersRepo.findUserByName(message.split("\\|")[1]);
        List<Room> rooms = roomsRepo.findRoomByUserName(user.getName());
        String roomId;
        if (rooms.size() < 1) {
            roomId = "empty";
        } else {
            roomId = rooms.stream().findFirst().get().getRoomId();
        }
        output.println(roomId);
    }




    public static List<String> stringToListParser(String input) {
        return Arrays.stream(input.split("#")).collect(Collectors.toList());
    }



}