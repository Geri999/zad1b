package chat.commons.entities;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "ROOM")
@NoArgsConstructor
@Getter
@Setter
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROOM_ID")
    private Long roomId;


    @Column(name = "ROOM_NAME")
    private String roomName;

    @Column(name = "USERS_IN_ROOM")
    @ToString.Exclude
    @OneToMany(mappedBy = "room")
    private Set<User> usersInRoom;

    public Room(String roomName) {
        this.roomName = roomName;
    }

    public void broadcastToAllRoomParticipant(String chatMessage) {
        try {
            for (User user : usersInRoom) {
                Socket socket = user.getSocket();
                new PrintWriter(socket.getOutputStream(), true).println(chatMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String userListToString() {
        return usersInRoom.stream().map(User::getUserName).collect(Collectors.joining("^"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Room room = (Room) o;
        return roomId != null && Objects.equals(roomId, room.roomId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}