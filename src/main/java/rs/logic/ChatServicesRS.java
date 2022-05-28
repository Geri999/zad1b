package rs.logic;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rs.entities.User;
import rs.repository.RoomRepoRS;
import rs.repository.UserRepoRS;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class ChatServicesRS {

    @Inject
    private UserRepoRS userRepoRS;

    @Inject
    private RoomRepoRS roomRepoRS;





/*    public static EntityManagerFactory emf = Persistence.createEntityManagerFactory("defaultH2");


    public ChatServicesRS() {
        this.emf = Persistence.createEntityManagerFactory("defaultH2");
    }*/

    public void createWaitingRoomWithAdmin() {
        roomRepoRS.createWaitingRoomWithAdmin();
        log.info("GP: createWaitingRoomWithAdmin()");
    }

    public Long loginRequest(String userName) {
        Long userId = userRepoRS.addUser(new User(userName));
        log.info("GP: User was logged and added to Waiting Room");
        return userId;
    }

    public String currentUserListRequest() {
        String userList = userRepoRS.findAllCurrentUsers().stream().map(User::getUserName).collect(Collectors.joining("|"));
//        output.println(collect);
        log.info("GP: User list was prepared and sent to Client");
        return userList;
    }


    public void logoutRequest(String userName) {

        userRepoRS.removeUserFromRoom(userName);

        //todo zamknij pokój gdy było ich tylko 2, odejmij z pokojów wielosobowych, odejmij z listy klientów
        log.info("GP: (Logout) User was removed from usersRepo");
    }


    public Long createChatRoomWithUsersRequest(String roomName, List<String> userList) {

        List<User> userStream = userList.stream()
                .map(s -> userRepoRS.findUserByName(s))
                .peek(System.out::println)
                .toList();
        Long roomId = roomRepoRS.createRoom(roomName);
        log.info("T: pointer 4a: {}", userList);

        userStream.forEach(u -> userRepoRS.moveUserToRoom(u, roomId));
        log.info("T: pointer 4b");

        return roomId;
    }


    public void addUserByIdToExistingRoomById(Long roomId, Long userId) {

        User user = userRepoRS.findUserById(userId);
        userRepoRS.moveUserToRoom(user, roomId);
    }
}
