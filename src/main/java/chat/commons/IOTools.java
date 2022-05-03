package chat.commons;

import chat.server.repository.UsersRepo;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class IOTools {
    static String separator = File.separator;
    static String downloadFolder = "download";

    private static String mainPath = (new File("").getAbsolutePath() + "\\src\\main\\resources\\");

    public static HashMap<String, String> loadConfigFile() {

        HashMap<String, String> configMap = new HashMap<>();
        File file = new File(mainPath + "config_file.cfg");
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader);
        ) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] array = line.split("=");
                try {
                    configMap.put(array[0], array[1]);
                } catch (Exception e) {
                    log.info("Empty line or no \"=\" sign in config_file.cfg");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("Configfile loaded. {}", configMap
                .entrySet()
                .stream()
                .map(s -> (s.getKey() + "=" + s.getValue()))
                .collect(Collectors.joining(" | ")));

        return configMap;
    }

    public static void saveMessageToFile(String message, Room room) {
        String users = room.userListToString();
        String[] sm = message.split("\\|");
        String sender = sm[1];
        String text = sm[3];

        File file = new File(mainPath + "conversation.txt");

        try (FileWriter fileWriter = new FileWriter(file, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        ) {
            bufferedWriter.write(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss")) + "|" + sender + "|" + text + "|" + users + "\n");
            bufferedWriter.flush();
            log.info("Text save to file: {}", message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> loadMessageFromFile(String userName) {
        File file = new File(mainPath + "conversation.txt");
        List<String> collectedConversations = new ArrayList<>();

        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader);
        ) {

            collectedConversations = bufferedReader
                    .lines()
                    .filter(l -> l.split("\\|")[3].contains(userName))
                    .map(z -> {
                        String[] split1 = z.split("\\|");
                        return String.format("[%s] %s: %s", split1[0], split1[1].equals(userName) ? split1[1] : " ".repeat(40) + split1[1], split1[2]);
                    }).collect(Collectors.toList());

        } catch (IOException e) {
            collectedConversations.add("File doesn't exist!");
        }
        return collectedConversations;
    }

    public static void sendFile(Socket socket) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter file name:");
        String fileName = sc.nextLine();
        System.out.println("Who do you want to send file to:");
        String recipientName = sc.nextLine();

        try/* (
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        ) */ {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            File file = new File(mainPath + separator + fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            dataOutputStream.writeUTF(recipientName);
            dataOutputStream.writeUTF(fileName);
            dataOutputStream.writeLong(file.length());

            int readBytes = 0;
            byte[] buffer = new byte[4 * 1024];
            while ((readBytes = fileInputStream.read(buffer)) != -1) {//blocking !!!!!!!!!!!!!!!!!!!
                dataOutputStream.write(buffer, 0, readBytes);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.printf("File %s sent to %s\n", fileName, recipientName);
    }

    public static void receiveAndSaveFile(Socket socket, UsersRepo usersRepo) {

        try
//                (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream()))
//             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String recipientName = dataInputStream.readUTF();
            String fileName = dataInputStream.readUTF();
            long sizeOfFile = dataInputStream.readLong();
            log.info("Odbiór pliku: długość: {}, odbiorca:{}, nazwa pliku: {}", sizeOfFile, recipientName, fileName);

            User userByRecipientName = usersRepo.findUserByName(recipientName);
            log.info("Odbiór pliku: znaleziony po Name:{},{}", userByRecipientName.getName(), userByRecipientName.getSocket());

            File file = new File(mainPath + separator + downloadFolder + separator + fileName);

            while (file.exists()) {
                file = new File(mainPath + separator + downloadFolder + separator + renameFileToAvoidDuplication(file.getName()));
                log.info("filename={}", fileName);
                log.info("file.getName()={}", file.getName());
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            log.info("nazwa pliku do nagrania: {}, path: {}", file.getName(), file.getPath());
            int readBytes = 0;
            byte[] buffer = new byte[4 * 1024];
            while (sizeOfFile > 0
                    &&
                    (readBytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, sizeOfFile))) != -1) {//blocking !!!!!!!!!!!!!!!!!!!
                fileOutputStream.write(buffer, 0, readBytes);
                sizeOfFile -= readBytes;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String renameFileToAvoidDuplication(String fileName) {
        String[] fileNameArray = fileName.split("\\.");
        String fileNameWithoutExtension = fileNameArray.length == 1 ? fileName : Arrays.stream(fileNameArray, 0, fileNameArray.length - 1)
                .collect(Collectors.joining("."));
        String fileNameExtension = fileNameArray.length > 1 ? "." + fileNameArray[fileNameArray.length - 1] : "";

        final String regex = "^(.*)(\\(\\d+\\))$";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(fileNameWithoutExtension);

        int i = 1;
        String group = fileNameWithoutExtension;
        if (matcher.find()) {
            if (matcher.groupCount() > 1) {
                i += Integer.parseInt(matcher.group(2).replace("(", "").replace(")", ""));
            }
            group = matcher.group(1);
        }

        return String.format("%s(%d)%s", group, i, fileNameExtension);
    }
}