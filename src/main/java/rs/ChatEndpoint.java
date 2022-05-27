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
    }

    @GET
    @Path("/login/{newUser}")
    @Produces("text/plain")
    public String login(@PathParam("newUser") String userName) {
        chatServicesRS.loginRequest(userName);
        log.info("GP: Login: {}", userName);
        return String.format("User %s added", userName);
    }

    @GET
    @Path("/userlist")
    @Produces("text/plain")
    public String userListRequest() {
        log.info("GP: userList:");
        return String.format("Users list: %s", chatServicesRS.currentUserListRequest());
    }

    @GET
    @Path("/logout/{newUser}")
    @Produces("text/plain")
    public String logout(@PathParam("newUser") String userName) {
        log.info("GP: Logout");
        chatServicesRS.logoutRequest(userName);
        return String.format("User %s removed", userName);
    }


    @GET
    @Path("/chatP2P")
    @Produces("text/plain")
    public void chatP2P(@QueryParam("user") String user) {
        log.info("GP: chatP2P with 1 user: {}", user);
    }

    @GET
    @Path("/chatM2M")
    @Produces("text/plain")
    public void chatM2M(@QueryParam("listOfUsers") List<String> listOfUsers) {
        log.info("GP: chatM2M list 1 user: {}", listOfUsers);
    }

    @GET
    @Path("/file")
    @Produces("text/plain")
    public void chatM2M(@QueryParam("operation") String operation,
                        @QueryParam("path") String path,
                        @QueryParam("receiver") String receiver) {
        log.info("GP: Send/Load file: {} {}", operation, path);
    }
}