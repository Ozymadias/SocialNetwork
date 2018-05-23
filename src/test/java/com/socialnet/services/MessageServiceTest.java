package com.socialnet.services;

import com.socialnet.pojos.Message;
import com.socialnet.pojos.User;
import com.socialnet.pojos.UserMessage;
import com.socialnet.repositories.UserRepository;
import org.junit.Assert;
import org.mockito.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;

public class MessageServiceTest {
    @InjectMocks
    private MessageService messageService;

    @Mock
    private UserRepository userRepository;
    private List<User> users;
    private UserMessage[] userMessages;

    @BeforeMethod
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod(groups = "messages")
    public void initUsersWithMessages() {
        User user1 = new User("name1", "city", "1900-1-1");
        user1.setId("first");
        User user2 = new User("name2", "city", "1900-1-2");
        user2.setId("second");
        User user3 = new User("name3", "city", "1900-1-3");
        user3.setId("third");

        userMessages = new UserMessage[6];

        addMessageNumberToUser(user1, 1);
        addMessageNumberToUser(user2, 2);
        addMessageNumberToUser(user3, 3);
        addMessageNumberToUser(user2, 4);
        addMessageNumberToUser(user1, 5);
        addMessageNumberToUser(user3, 6);

        users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
    }

    @Test
    public void postMessage() {
        User user = new User("name", "city", "1900-1-1");
        Mockito.when(userRepository.findById("1")).thenReturn(user);

        String messageContent = "messageContent";
        messageService.postMessage("1", messageContent);

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(argumentCaptor.capture());
        User capturedUser = argumentCaptor.getValue();

        Assert.assertEquals(user, capturedUser);
        Assert.assertEquals(1, capturedUser.getMessages().size());
        Assert.assertEquals(messageContent, capturedUser.getMessages().get(0).getContent());
    }

    @Test(groups = "messages")
    public void receiveAllMessages() {
        Mockito.when(userRepository.findAll()).thenReturn(users);

        Assert.assertThat(messageService.receiveAllMessages(), is(userMessages));
    }

    @Test(groups = "messages")
    public void receiveMessages() {
        Mockito.when(userRepository.findAll(any(Set.class))).thenReturn(users);

        Assert.assertThat(messageService.receiveMessages(new HashSet<>()), is(userMessages));
    }

    private void addMessageNumberToUser(User user, int i) {
        Message message = new Message(i, String.valueOf(i));
        user.getMessages().add(message);
        userMessages[i - 1] = new UserMessage(user.getId(), message);
    }

}