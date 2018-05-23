package com.socialnet.services;

import com.socialnet.pojos.User;
import com.socialnet.repositories.UserRepository;
import org.junit.Assert;
import org.mockito.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class MessageServiceTest {
    @InjectMocks
    private MessageService messageService;

    @Mock
    private UserRepository userRepository;

    @BeforeMethod
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
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

}