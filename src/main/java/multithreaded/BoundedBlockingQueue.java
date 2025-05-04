package multithreaded;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

class CustomBoundedBlockingQueue {

    private final Queue<Integer> q;
    private final Semaphore producer;
    private final Semaphore consumer;
    private final Semaphore mutex;

    public CustomBoundedBlockingQueue(int capacity) {
        q = new LinkedList<>();
        consumer = new Semaphore(0);
        producer = new Semaphore(capacity);
        mutex = new Semaphore(1);
    }

    public void produce(Integer i) throws InterruptedException {
        producer.acquire();
        mutex.acquire();
        q.add(i);
        mutex.release();
        consumer.release();
    }

    public int consume() throws InterruptedException {
        consumer.acquire();
        mutex.acquire();
        try {
            return q.remove();
        } finally {
            mutex.release();
            producer.release();
        }
    }

}


public class BoundedBlockingQueue {

    public static void main(String[] args) {
        CustomBoundedBlockingQueue customBoundedBlockingQueue = new CustomBoundedBlockingQueue(10);

        ExecutorService consumers = Executors.newFixedThreadPool(3);
        Runnable consumer = () -> {
            while (true) {
                Integer num = null;
                try {
                    num = customBoundedBlockingQueue.consume();
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
            for (int i = 0; i < 100; i++) {
                try {
                    customBoundedBlockingQueue.produce(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }
}
