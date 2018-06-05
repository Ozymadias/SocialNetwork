package com.socialnet.services;

import com.socialnet.pojos.Node;
import com.socialnet.repositories.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NodeService {
    @Autowired
    NodeRepository nodeRepository;

    public void insert(String mongoId) {
        nodeRepository.save(new Node(mongoId));
    }

    public void unfriend(String userId, String friendId) {
        nodeRepository.unfriend(userId, friendId);
    }

    public void sendInvitation(String userId, String inviteeId) {
        nodeRepository.sendInvitation(userId, inviteeId);
    }

    public Collection<Node> invitations(String userId) {
        return nodeRepository.invitations(userId);
    }

    public Collection<Node> friends(String userId) {
        return nodeRepository.friends(userId);
    }

    public void acceptInvitation(String userId, String inviterId) {
        nodeRepository.friend(userId, inviterId);
    }

    public Collection<Node> network(String userId) {
        return nodeRepository.network(userId);
    }

    public void declineInvitation(String userId, String inviterId) {
        nodeRepository.declineInvitation(userId, inviterId);
    }

    public Set<String> findFriends(String userId) {
        return nodeRepository.friends(userId).stream().map(Node::getMongoId).collect(Collectors.toSet());
    }

    public Set<String> findNetwork(String userId) {
        return nodeRepository.network(userId).stream().map(Node::getMongoId).collect(Collectors.toSet());
    }

    public Integer computeDistance(String userId, String otherId) throws InterruptedException {
        Thread.sleep(2000);
        return Optional.ofNullable(nodeRepository.distanceFactor(userId, otherId)).orElse(0);
    }
}
