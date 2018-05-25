package com.socialnet.repositories;

import com.socialnet.pojos.Node;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class NodeRepositoryTest {
    @Autowired
    private NodeRepository nodeRepository;

    @Test
    public void databaseShouldContainsUserAfterInsertingOne() {
        Node node = new Node("mongoId");
        nodeRepository.save(node);
        Iterable<Node> all = nodeRepository.findAll();
        assertThat(all, contains(node));
    }
}