package chat.commons;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class MessageMapper {

/*    private Commands command;
    private User sender;
    private Room destinationRoom;
    private String messageStrings;*/

    //Command to String
    public static String messageConverter(Commands command, String senderName, Long roomId, String messageContent) {
        return command + "|" + senderName + "|" + roomId.toString() + "|" + messageContent;
    }


    public static String createLoginMessage(Commands command, String userName) {
        return command + "|" + userName;
    }

    public static String createAllUsersListMessage(Commands command) {
        return command.toString();
    }

    public static String createRequestForRoomMessage(Commands command, List<String> roomUsers) {
        return command + "|" + roomUsers.stream().collect(Collectors.joining("#"));
    }


    //String to Command parser

    public static String createChatTxtMessage(Commands command, String sender, String roomId, String text) {
        return command + "|" + sender + "|" + roomId + "|" + text;
    }

    public static String createExitMessage(Commands command, String userName) {
        return command + "|" + userName;
    }

    public static String createFindRoomIdByUserNameMessage(Commands command, String userName) {
        return command + "|" + userName;
    }


/*    public static List<String> stringToListParser(String input) {
        return Arrays.stream(input.split("#")).collect(Collectors.toList());
    }*/

    //##############################################################################################################


//    @Override
//    public String toString() {
//        return command + "|" + sender + "|" + destinationRoom + "|" + messageStrings;
//    }
}