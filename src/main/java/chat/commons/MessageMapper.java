package chat.commons;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class MessageMapper {

    private Commands command;
    private User sender;
    private Room destinationRoom;
    private String messageStrings;

    public static String createLoginMessage(Commands command, String userName, Socket socket) {
        return command + "|" + userName + "|" + socket.getInetAddress() + "#" + socket.getPort() + "#" + socket.getLocalPort();
    }

    public static String createAllUsersListMessage(Commands command) {
        return command.toString();
    }

    public static String createRequestForRoomMessage(Commands command, List<String> roomUsers) {
        return command + "|" + roomUsers.stream().collect(Collectors.joining("#"));
    }

    public static List<String> stringToListParser(String input) {
        return Arrays.stream(input.split("#")).collect(Collectors.toList());
    }

    public static String createChatTxtMessage(Commands command, String sender, String roomId, String text) {
                return command + "|" +sender + "|" +  roomId + "|" +text;
    }

    public static String createExitMessage(Commands command, String userName) {
        return command + "|" + userName;
    }

    public static String createFindRoomIdByUserNameMessage(Commands command, String userName) {
        return command + "|" + userName;
    }

    public static void printFormattedMessageOnConsole(String sender, String messageStrings) {
        String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss"));
        System.out.printf("[%s from %s]: %s\n", timeNow, sender, messageStrings);
    }

    @Override
    public String toString() {
        return command + "|" + sender + "|" + destinationRoom + "|" + messageStrings;
    }
}