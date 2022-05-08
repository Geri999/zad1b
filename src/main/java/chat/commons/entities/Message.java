package chat.commons.entities;

import chat.commons.Commands;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MESSAGE")

public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MESSAGE_ID")
    private Long message_id;

    @Column(name = "COMMAND")
    private Commands command;

    @ManyToOne
    @JoinColumn(name ="USER_ID")
    private User sender;

    @Transient
    private Long destinationRoomId;

    @Column(name = "MESSAGE_CONTENET")
    private String messageContent;

    public Message(Commands command, User sender, Long destinationRoomId, String messageContent) {
        this.command = command;
        this.sender = sender;
        this.destinationRoomId = destinationRoomId;
        this.messageContent = messageContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Message message = (Message) o;
        return message_id != null && Objects.equals(message_id, message.message_id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
