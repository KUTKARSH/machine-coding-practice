package SlackChatApp;

import java.util.*;

// Enums
enum MessageType {
    TEXT, FILE
}

// SlacChatApp.User class
class User {
    private final String id;
    private final String name;
    private final String email;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name;
    }
}

// SlacChatApp.Message class
class Message {
    private final String id;
    private final User sender;
    private String content;
    private final Date timestamp;
    private final MessageType type;
    private boolean deleted = false;
    private final Set<User> seenBy = new HashSet<>();
    private final Map<String, Set<User>> reactions = new HashMap<>();

    public Message(String id, User sender, String content, MessageType type) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.timestamp = new Date();
        this.type = type;
    }

    public String getId() { return id; }
    public User getSender() { return sender; }

    public void edit(String newContent) {
        this.content = newContent;
    }

    public void delete() {
        this.deleted = true;
        this.content = "<deleted>";
    }

    public void markSeen(User user) {
        seenBy.add(user);
    }

    public void addReaction(String emoji, User user) {
        reactions.putIfAbsent(emoji, new HashSet<>());
        reactions.get(emoji).add(user);
    }

    public void display() {
        System.out.print("[" + timestamp + "] " + sender.getName() + ": " + content);
        if (!reactions.isEmpty()) {
            System.out.print("  ");
            reactions.forEach((emoji, users) -> {
                System.out.print(emoji + " " + users.size() + " ");
            });
        }
        System.out.println();
    }
}

// SlacChatApp.Channel class
class Channel {
    private final String id;
    private final String name;
    private final boolean isPrivate;
    private final Set<User> members = new HashSet<>();
    private final List<Message> messages = new ArrayList<>();

    public Channel(String id, String name, boolean isPrivate) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
    }

    public void addMember(User user) {
        members.add(user);
    }

    public void sendMessage(User sender, String content, MessageType type) {
        if (!members.contains(sender)) {
            System.out.println("Access denied for " + sender.getName());
            return;
        }

        Message msg = new Message(UUID.randomUUID().toString(), sender, content, type);
        messages.add(msg);
        notifyUsers(msg);
    }

    private void notifyUsers(Message msg) {
        for (User user : members) {
            if (!user.equals(msg.getSender())) {
                System.out.println("[Notification] New message in #" + name + " for " + user.getName());
            }
        }
    }

    public void showMessages() {
        System.out.println("SlacChatApp.Channel: #" + name);
        for (Message m : messages) {
            m.display();
        }
    }
}

// SlacChatApp.PrivateChat class
class PrivateChat {
    private final User user1;
    private final User user2;
    final List<Message> messages = new ArrayList<>();

    public PrivateChat(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public void sendMessage(User sender, String content, MessageType type) {
        if (!sender.equals(user1) && !sender.equals(user2)) return;

        Message msg = new Message(UUID.randomUUID().toString(), sender, content, type);
        messages.add(msg);
        System.out.println("[Private] " + sender.getName() + " -> " +
                (sender.equals(user1) ? user2.getName() : user1.getName()));
    }

    public void editMessage(String messageId, String newContent, User editor) {
        for (Message msg : messages) {
            if (msg.getId().equals(messageId) && msg.getSender().equals(editor)) {
                msg.edit(newContent);
                return;
            }
        }
        System.out.println("Edit failed. SlacChatApp.Message not found or not owned by user.");
    }

    public void deleteMessage(String messageId, User requester) {
        for (Message msg : messages) {
            if (msg.getId().equals(messageId) && msg.getSender().equals(requester)) {
                msg.delete();
                return;
            }
        }
        System.out.println("Delete failed. SlacChatApp.Message not found or not owned by user.");
    }

    public void reactToMessage(String messageId, String emoji, User user) {
        for (Message msg : messages) {
            if (msg.getId().equals(messageId)) {
                msg.addReaction(emoji, user);
                return;
            }
        }
        System.out.println("SlacChatApp.Message not found.");
    }

    public void showMessages() {
        System.out.println("Private chat between " + user1.getName() + " and " + user2.getName());
        for (Message m : messages) {
            m.display();
        }
    }
}

// SlacChatApp.Workspace class
class Workspace {
    private final String id;
    private final String name;
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Channel> channels = new HashMap<>();
    private final List<PrivateChat> privateChats = new ArrayList<>();

    public Workspace(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public void createChannel(String channelId, String name, boolean isPrivate) {
        channels.put(channelId, new Channel(channelId, name, isPrivate));
    }

    public Channel getChannel(String channelId) {
        return channels.get(channelId);
    }

    public PrivateChat createPrivateChat(User u1, User u2) {
        PrivateChat chat = new PrivateChat(u1, u2);
        privateChats.add(chat);
        return chat;
    }
}

// Main class
public class SlackChatApp {
    public static void main(String[] args) {
        Workspace workspace = new Workspace("w1", "DevTeam");

        User alice = new User("u1", "Alice", "alice@x.com");
        User bob = new User("u2", "Bob", "bob@x.com");
        User charlie = new User("u3", "Charlie", "charlie@x.com");

        workspace.addUser(alice);
        workspace.addUser(bob);
        workspace.addUser(charlie);

        workspace.createChannel("c1", "general", false);
        Channel general = workspace.getChannel("c1");
        general.addMember(alice);
        general.addMember(bob);

        general.sendMessage(alice, "Welcome to the general channel!", MessageType.TEXT);
        general.sendMessage(bob, "Thanks Alice!", MessageType.TEXT);
        general.showMessages();

        // Private chat
        PrivateChat chat = workspace.createPrivateChat(alice, bob);
        chat.sendMessage(alice, "Hey Bob!", MessageType.TEXT);
        chat.sendMessage(bob, "Hello Alice!", MessageType.TEXT);

        String msgId = chat.messages.get(0).getId();
        chat.editMessage(msgId, "Hey Bob! (edited)", alice);
        chat.reactToMessage(msgId, "👍", bob);
        chat.showMessages();

        chat.deleteMessage(msgId, alice);
        chat.showMessages();
    }
}