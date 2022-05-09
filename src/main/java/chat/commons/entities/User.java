package chat.commons.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "CHAT_USER")
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID")
    private Long user_id;

    @Column(name = "USER_NAME", nullable = false)
    private String userName;

    @Transient
    private Socket socket;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @ManyToMany(mappedBy = "messageReceiversUserSet")
    private Set<MessageTxt> messagesList;


    public User(String userName, Socket socket) {
        this.userName = userName;
        this.socket = socket;
    }

    @Override
    public String toString() {
        return userName + "#" + socket.getInetAddress() + "#" + socket.getPort() + "#" + socket.getLocalPort();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return user_id != null && Objects.equals(user_id, user.user_id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
