package core;

import models.Message;

import java.util.*;
import java.util.stream.Collectors;

public class DiscordImpl implements Discord {
    private final Map<String, Message> messagesById;
    private final Map<String, LinkedHashSet<Message>> messagesByChannel;

    public DiscordImpl() {
        this.messagesById = new LinkedHashMap<>();
        this.messagesByChannel = new HashMap<>();
    }

    @Override
    public void sendMessage(Message message) {
        this.messagesById.put(message.getId(), message);

        this.messagesByChannel.putIfAbsent(message.getChannel(), new LinkedHashSet<>());
        this.messagesByChannel.get(message.getChannel()).add(message);
    }

    @Override
    public boolean contains(Message message) {
        return messagesById.containsKey(message.getId());
    }

    @Override
    public int size() {
        return messagesById.size();
    }

    @Override
    public Message getMessage(String messageId) {
        Message result = messagesById.get(messageId);
        if (result == null) {
            throw new IllegalArgumentException();
        }

        return result;
    }

    @Override
    public void deleteMessage(String messageId) {
        Message result = messagesById.remove(messageId);
        if (result == null) {
            throw new IllegalArgumentException();
        }

        this.messagesByChannel.get(result.getChannel()).remove(result);
    }

    @Override
    public void reactToMessage(String messageId, String reaction) {
        Message message = this.getMessage(messageId);
        message.getReactions().add(reaction);
    }

    @Override
    public Iterable<Message> getChannelMessages(String channel) {
        Set<Message> result = this.messagesByChannel.get(channel);
        if (result == null) {
            throw new IllegalArgumentException();
        }

        return result;
    }

    @Override
    public Iterable<Message> getMessagesByReactions(List<String> reactions) {
        return messagesById.values()
                .stream()
                .filter(m -> new HashSet<>(m.getReactions()).containsAll(reactions))
                .sorted((m1, m2) -> {
                    if (m1.getReactions().size() != m2.getReactions().size()) {
                        return m2.getReactions().size() - m1.getReactions().size();
                    }

                    return m1.getTimestamp() - m2.getTimestamp();
                })
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Message> getMessageInTimeRange(Integer lowerBound, Integer upperBound) {
        List<Message> result = messagesById.values().stream()
                .filter(m -> m.getTimestamp() >= lowerBound && m.getTimestamp() <= upperBound)
                .sorted((m1, m2) -> {
                    int m1ChannelMessageCount = this.messagesByChannel.get(m1.getChannel()).size();
                    int m2ChannelMessageCount = this.messagesByChannel.get(m2.getChannel()).size();

                    return m2ChannelMessageCount - m1ChannelMessageCount;
                })
                .collect(Collectors.toList());

        return result;
    }

    @Override
    public Iterable<Message> getTop3MostReactedMessages() {
        return messagesById.values()
                .stream()
                .sorted((m1, m2) -> m2.getReactions().size() - m1.getReactions().size())
                .limit(3)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Message> getAllMessagesOrderedByCountOfReactionsThenByTimestampThenByLengthOfContent() {
        return messagesById.values()
                .stream()
                .sorted((m1, m2) -> {
                    int m1ReactionsSize = m1.getReactions().size();
                    int m2ReactionsSize = m2.getReactions().size();

                    if (m1ReactionsSize != m2ReactionsSize) {
                        return m2ReactionsSize - m1ReactionsSize;
                    }

                    if (m1.getTimestamp() != m2.getTimestamp()) {
                        return m1.getTimestamp() - m2.getTimestamp();
                    }

                    return m1.getContent().length() - m2.getContent().length();
                })
                .collect(Collectors.toList());
    }
}
