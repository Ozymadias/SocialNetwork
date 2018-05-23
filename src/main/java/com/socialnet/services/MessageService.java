package com.socialnet.services;

import com.socialnet.pojos.Message;
import com.socialnet.pojos.User;
import com.socialnet.pojos.UserMessage;
import com.socialnet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class MessageService {
    @Autowired
    UserRepository userRepository;

    private Function<User, Stream<? extends UserMessage>> userToUserMessage = user ->
            user.getMessages().stream().map(message -> new UserMessage(user.getId(), message));


    public void postMessage(String userId, String body) {
        Message message = new Message(System.currentTimeMillis(), body);
        User user = userRepository.findById(userId);
        user.getMessages().add(message);
        userRepository.save(user);
    }

    public UserMessage[] receiveAllMessages() {
        return getUsersMessages(userRepository.findAll().stream());
    }

    public UserMessage[] receiveMessages(Set<String> ids) {
        Iterable<User> all = userRepository.findAll(ids);
        return getUsersMessages(StreamSupport.stream(all.spliterator(), false));
    }

    private UserMessage[] getUsersMessages(Stream<User> userStream) {
        UserMessage[] messages = userStream.flatMap(userToUserMessage).toArray(UserMessage[]::new);
        Arrays.sort(messages);
        return messages;
    }
}
