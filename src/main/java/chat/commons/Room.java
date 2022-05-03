package chat.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Slf4j
@AllArgsConstructor

public class Room {

    private String roomId;
    private List<User> userListInRoom = new ArrayList<>();

    public Room() {
        this.roomId = UUID.randomUUID().toString();
    }

    public void broadcastToAllRoomParticipant(String chatMessage) {
        try {
            for (User user : userListInRoom) {
                Socket socket = user.getSocket();
                new PrintWriter(socket.getOutputStream(), true).println(chatMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String userListToString() {
        return userListInRoom.stream().map(User::getName).collect(Collectors.joining("^"));
    }
}