package chat.commons;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ROOM2")
@NoArgsConstructor
@AllArgsConstructor
public class Room2 {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ROOM_ID")
    private Long roomId;


    @Column(name = "ROOM_NAME")
    private String roomName;

    @OneToMany(mappedBy = "room")
    @Column(name = "USERS_IN_ROOM")
    @Getter
    private Set<User2> userInRoom = new HashSet<>();

    public Room2(String roomName) {
        this.roomName = roomName;
    }

    public Long getRoomId() {
        return roomId;
    }
}