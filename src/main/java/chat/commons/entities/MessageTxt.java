package chat.commons.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MESSAGE_TXT")
public class MessageTxt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MESSAGE_ID")
    private Long message_id;

    @ManyToMany
    @JoinTable(name = "messagetxt_user",
            joinColumns = { @JoinColumn(name = "fk_messagetxt", referencedColumnName = "MESSAGE_ID") },
            inverseJoinColumns = { @JoinColumn(name = "fk_user", referencedColumnName = "USER_ID") })
    private Set<User> messageReceiversUserSet;

    @Column(name = "MESSAGE_CONTENET")
    private String messageContent;


    public MessageTxt(Set<User> messageReceiversUserSet, String messageContent) {
        this.messageReceiversUserSet = messageReceiversUserSet;
        this.messageContent = messageContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MessageTxt that = (MessageTxt) o;
        return message_id != null && Objects.equals(message_id, that.message_id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
