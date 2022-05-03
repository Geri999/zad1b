package chat.server;

import chat.commons.User;
import chat.server.repository.UsersRepo;
import chat.commons.IOTools;
import chat.commons.MessageMapper;
import chat.commons.Room;
import chat.server.repository.MessagesRepo;
import chat.server.repository.RoomsRepo;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
@Builder
public class ServerAPI {

    private final MessagesRepo messagesRepo;
    private final RoomsRepo roomsRepo;
    private final UsersRepo usersRepo;
    private final Socket socket;

    public void commandInterpreter(String message, PrintWriter output) {
        String command = message.split("\\|")[0];
        log.info(command);
        switch (command) {
            case "$LOGIN_REQUEST":
                loginUserAndReturnInfoToClient(message, output);
                break;
            case "$USERS_LIST_REQUEST":
                createAndReturnUserListToServer(output);
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
                receiveAndBrodcastChatText(message);
                break;
            case "$SEND_FILE_MSG":
                IOTools.receiveAndSaveFile(socket, usersRepo);
                break;
            case "$LEAVING_THE_ROOM_REQUEST":
                receiveAndBrodcastChatText(message);
                //todo
                usersRepo.removeUserFromRoom(message);
                break;
        }
    }

    private void receiveAndBrodcastChatText(String message) {
        String[] splitChat = message.split("\\|");
        String roomId = splitChat[2];
        String text = splitChat[3];

        log.info("case=$BROADCAST_TEXT_MSG, room id={}", roomId);

        Room roomById = roomsRepo.findRoomById(roomId);

        roomById.broadcastToAllRoomParticipant(message);
        IOTools.saveMessageToFile(message, roomById);
        log.info("broadcasting text={}", message);
    }

    private void createRoomAndReturnInfoToClient(String message, PrintWriter output) {
        String s = message.split("\\|")[1];
        List<String> userNameForRoomList = MessageMapper.stringToListParser(s);

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

    private void logoutUserRequest(String message, PrintWriter output) {
        User userByName = usersRepo.findUserByName(message.split("\\|")[1]);
        boolean operationResult = usersRepo.getUserLists().remove(userByName);
        output.println(operationResult);
        //todo zamknij pokój gdy było ich tylko 2, odejmij z pokojów wielosobowych, odejmij z listy klientów
        //ale to jest logout, to nie trzeba gdy robi logout, bo wyjsce z czatu to go kasuje z pokoii.
        log.info("User removed from usersRepo"); //todo second condition, when "false"
    }

    private void createAndReturnUserListToServer(PrintWriter output) {
        String collect = usersRepo.getUserLists().stream().map(User::getName).collect(Collectors.joining("|"));
        output.println(collect);
        log.info("User list prepared and sent");
    }

    private void loginUserAndReturnInfoToClient(String message, PrintWriter output) {
        String[] split = message.split("\\|");
        String userName = split[1];
        Socket socketFromInput = socket;
        usersRepo.addUser(new User(userName, socketFromInput));
        output.println("true");
        log.info("User added");
    }
}