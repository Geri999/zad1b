package rs;

import lombok.extern.slf4j.Slf4j;
import rs.logic.ChatServicesRS;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Path("/")
public class ChatEndpoint {


    @Inject
    private ChatServicesRS chatServicesRS;

    @GET
    @Path("/welcome")
    @Produces("text/plain")
    public String welcome() {
        log.info("GP: App welcome page started");
        chatServicesRS.createWaitingRoomWithAdmin();
        return "Chat 1.1 Welcome.\nDATETIME: " + LocalDateTime.now();
        //http://localhost:8080/chat-1/chatAPI/welcome
    }

    @GET
    @Path("/login/{newUser}")
    @Produces("text/plain")
    public String login(@PathParam("newUser") String userName) {
        Long userId = chatServicesRS.loginRequest(userName);
        log.info("GP: Login: {}", userName);
        return String.format("User %s added with ID: %d (that ID will be save be Client", userName, userId);
    //http://localhost:8080/chat-1/chatAPI/login/Gerard
    }

    @GET
    @Path("/userlist")
    @Produces("text/plain")
    public String userListRequest() {
        log.info("GP: userList:");
        return String.format("Users list: %s", chatServicesRS.currentUserListRequest());
        //http://localhost:8080/chat-1/chatAPI/userlist
    }

    @GET
    @Path("/logout/{newUser}")
    @Produces("text/plain")
    public String logout(@PathParam("newUser") String userName) {
        log.info("GP: Logout");
        chatServicesRS.logoutRequest(userName);
        return String.format("User %s removed", userName);
        //http://localhost:8080/chat-1/chatAPI/logout/Gerard
    }


    @GET
    @Path("/startChat")
    @Produces("text/plain")
    public String createChatRoomWithUsers(@QueryParam("roomName") String roomName, @QueryParam("userList") List<String> userList) {
        log.info("GP: chat started in room: {}, with users: {}", roomName,userList);

        Long roomId = chatServicesRS.createChatRoomWithUsersRequest(roomName, userList);

        return String.format("Chat started with users: %s in roomId: %d", userList, roomId);
        //http://localhost:8080/chat-1/chatAPI/startChat?roomName=NewRoom&userList=Gerard&userList=Tomek
    }

    @GET
    @Path("/addUserToChat")
    @Produces("text/plain")
    public String assUserToChat(@QueryParam("roomId") Long roomId,@QueryParam("userId") Long userId) {
        log.info("GP: Add user to Chat by roomId: {}, userId: {}", roomId, userId);

        chatServicesRS.addUserByIdToExistingRoomById(roomId, userId);
        return String.format("User with ID: {} added to chat in room with ID: {}", userId, roomId);
        //http://localhost:8080/chat-1/chatAPI/addUserToChat?roomId=5&userId=6
    }

    @GET
    @Path("/file")
    @Produces("text/plain")
    public void sendFile(@QueryParam("operation") String operation,
                        @QueryParam("path") String path,
                        @QueryParam("receiver") String receiver) {
        log.info("GP: Send/Load file: {} {}", operation, path);
    }
}