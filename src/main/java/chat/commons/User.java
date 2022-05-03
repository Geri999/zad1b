package chat.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;

@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String name;
    private Socket socket;

    @Override
    public String toString() {
        return name + "#" + socket.getInetAddress() + "#" + socket.getPort() + "#" + socket.getLocalPort();
    }

//    public static User parseToUser(String input) {
//        String[] userArrays = input.split("#");
//        String nameFromInput = userArrays[0];
//
//        Socket socketFromInput = null;
//        try {
//            socketFromInput = new Socket(userArrays[1], Integer.parseInt(userArrays[2]), null, Integer.parseInt(userArrays[3]));
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//
//        return new User(nameFromInput, socketFromInput);
//
//    }
}


