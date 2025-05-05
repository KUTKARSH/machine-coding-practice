package multithreaded;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

class Topic {

    @Getter
    private final String name;

    private final List<Subscriber> subscribers;

    public Topic(String name) {
        this.name = name;
        subscribers = new ArrayList<>();
    }

    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void publish(String msg) {
        for (Subscriber subscriber : subscribers) {
            subscriber.push(msg);
        }
    }
}

class Subscriber {

    private final LinkedBlockingQueue<String> q;

    @Getter
    private final String topicName;

    public Subscriber(String topicName) {
        q = new LinkedBlockingQueue<>();
        this.topicName = topicName;
    }

    public void push(String msg) {
        q.add(msg);
    }

    public String read() throws InterruptedException {
        String msg = q.take();
        return msg;
    }

}

class PubSubQueueSystem {

    private final ConcurrentHashMap<String, Topic> topicMap;

    public PubSubQueueSystem() {
        topicMap = new ConcurrentHashMap<String, Topic>();
    }

    public void subscribe(String topic, Subscriber subscriber) {
        Topic t = topicMap.get(topic);
        if (t != null) {
            t.subscribe(subscriber);
        } else {
            throw new IllegalArgumentException("Topic not found: " + topic);
        }
    }

    public void publish(String topic, String msg) {
        topicMap.get(topic).publish(msg);
    }

    public void addTopic(String name) {
        topicMap.put(name, new Topic(name));
    }

}

public class PubSubQueue {

    public static void main(String[] args) {

        // Setting up the message queue, adding topics and subscribers
        PubSubQueueSystem pubSubQueueSystem = new PubSubQueueSystem();
        pubSubQueueSystem.addTopic("news");
        pubSubQueueSystem.addTopic("sports");
        Subscriber newsSubscriber1 = new Subscriber("news");
        Subscriber newsSubscriber2 = new Subscriber("news");
        Subscriber sportsSubscribe1 = new Subscriber("sports");
        pubSubQueueSystem.subscribe("news", newsSubscriber1);
        pubSubQueueSystem.subscribe("news", newsSubscriber2);
        pubSubQueueSystem.subscribe("sports", sportsSubscribe1);

        // Starting consumer threads
        ExecutorService consumers = Executors.newFixedThreadPool(3);
        consumers.submit(() -> {
            while (true) {
                String msg = newsSubscriber1.read();
                System.out.println("News consumer: 1 | Message: " + msg);
            }
        });
        consumers.submit(() -> {
            while (true) {
                String msg = newsSubscriber2.read();
                System.out.println("News consumer: 2 | Message: " + msg);
            }
        });
        consumers.submit(() -> {
            while (true) {
                String msg = sportsSubscribe1.read();
                System.out.println("Sports consumer: 3 | Message: " + msg);
            }
        });

        // Starting publisher threads
        ExecutorService publishers = Executors.newFixedThreadPool(2);
        publishers.submit(() -> {
            while (true) {
                Integer num = new Random().nextInt(100);
                System.out.println("Publishing message: " + num + " to news topic");
                pubSubQueueSystem.publish("news", String.valueOf(num));
                Thread.sleep(1000);
            }
        });
        publishers.submit(() -> {
            while (true) {
                Integer num = new Random().nextInt(1000);
                System.out.println("Publishing message: " + num + " to sports topic");
                pubSubQueueSystem.publish("sports", String.valueOf(num));
                Thread.sleep(1000);
            }
        });

    }
}
