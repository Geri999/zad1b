package chat.commons;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "MESSAGE2")
@Builder(access = AccessLevel.PUBLIC)
//@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Message2 {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MESSAGE_ID")
    private int message_id;

    @Column(name = "COMMAND")
    private Commands command;

    @ManyToOne
    @JoinColumn(name ="USER_ID")
    private User2 sender;

//    private Room2 destinationRoom;

    @Column(name = "MESSAGE_CONTENET")
    private String messageContent;


}
