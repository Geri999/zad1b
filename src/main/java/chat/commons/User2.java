package chat.commons;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "USER2")
@NoArgsConstructor
public class User2 {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID")
    private int user_id;

    @Column(name = "USER_NAME", nullable = false)
    private String userName;

//    @Column(name = "ROOM")
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ROOM_ID")
    private Room2 room;

    @OneToMany(mappedBy = "sender")
    private List<Message2> messagesList = new LinkedList<>();



    public User2(String userName, Room2 room) {
        this.userName = userName;
        this.room = room;
    }
}
