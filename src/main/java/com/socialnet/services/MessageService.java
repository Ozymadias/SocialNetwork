package com.socialnet.services;

import com.socialnet.pojos.Message;
import com.socialnet.pojos.User;
import com.socialnet.pojos.UserMessage;
import com.socialnet.repository.UserRepository;
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
        UserMessage[] messages = userRepository.findAll().stream().flatMap(userToUserMessage).toArray(UserMessage[]::new);
        Arrays.sort(messages);
        return messages;
    }

    public UserMessage[] receiveMessages(Set<String> ids) {
        Iterable<User> all = userRepository.findAll(ids);
        UserMessage[] messages = StreamSupport.stream(all.spliterator(), false).flatMap(userToUserMessage).toArray(UserMessage[]::new);
        Arrays.sort(messages);
        return messages;
    }
}
