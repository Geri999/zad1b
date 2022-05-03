package chat.server.repository;

import chat.commons.MessageMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Data
@Slf4j
// method #3 by ConcurrentQueue
public class MessagesRepo {

    //todo
    private Queue<MessageMapper> conversationListNotSavedOnDisk = new ConcurrentLinkedQueue<>();
    private Queue<MessageMapper> conversationListLoadedFromDisk = new ConcurrentLinkedQueue<>();





}
