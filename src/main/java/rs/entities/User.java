package rs.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "CHAT_USER")
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USER_NAME", nullable = false)
    private String userName;

    //    @ManyToOne/*(cascade = {CascadeType.PERSIST, CascadeType.MERGE})*///*(fetch = FetchType.EAGER*//*, cascade = CascadeType.PERSIST*//*)*/
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROOM_ID", referencedColumnName = "ROOM_ID")
    private Room room;

    @ManyToMany(mappedBy = "messageReceiversUserSet")
    private Set<MessageTxt> messagesList;


    public void addMessageTxt(MessageTxt messageTxt) {
        if (messagesList == null) {
            messagesList = new HashSet<>();
        }
        messagesList.add(messageTxt);
        log.info("GP: messageTxt was added to User:{}{}", messageTxt, userId);
    }

    public User(String userName) {
        this.userName = userName;
    }


    @Override
    public String toString() {
        return "User{" + userId + "," + userName +",roomID:"+ room.getRoomId()+"}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return userId != null && Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
