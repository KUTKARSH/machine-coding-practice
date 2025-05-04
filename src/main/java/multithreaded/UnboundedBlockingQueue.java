package multithreaded;


import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

class MessageQueue {

    private final Queue<Integer> q;
    private final Semaphore mutex;
    private final Semaphore consumer;

    public MessageQueue() {
        q = new LinkedList<>();
        mutex = new Semaphore(1);
        consumer = new Semaphore(0);
    }

    public void produce(Integer i) throws InterruptedException {
        mutex.acquire();
        q.add(i);
        mutex.release();
        consumer.release();
    }

    public int consume() throws InterruptedException {
        consumer.acquire();
        try {
            mutex.acquire();
            return q.remove();
        } finally {
            mutex.release();
        }
    }

}

public class UnboundedBlockingQueue {

    public static void main(String[] args) {
        MessageQueue messageQueue = new MessageQueue();
        ExecutorService consumers = Executors.newFixedThreadPool(3);
        Runnable consumer = () -> {
            while (true) {
                Integer num = null;
                try {
                    num = messageQueue.consume();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(num);
            }
        };
        for (int i = 0; i < 3; i++) {
            consumers.submit(consumer);
        }

        ExecutorService producers = Executors.newCachedThreadPool();
        producers.submit(() -> {
            int num = 0;
            while (true) {
                try {
                    messageQueue.produce(num++);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
