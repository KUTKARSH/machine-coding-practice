package multithreaded;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
class Cluster {
    private Integer id;
    private AtomicInteger ram;
    private AtomicInteger cpu;
    private ExecutorService jobExecutor;

    public Cluster(Integer id, Integer ram, Integer cpu) {
        this.id = id;
        this.ram = new AtomicInteger(ram);
        this.cpu = new AtomicInteger(cpu);
        jobExecutor = Executors.newCachedThreadPool();
    }

    public void submit(Job job) {
        jobExecutor.submit(() -> {
            try {
                Thread.sleep(job.getTime());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                ram.addAndGet(job.getRam());
                cpu.addAndGet(job.getCpu());
            }
        });
    }
}


@Getter
@Setter
class ClusterManager {

    private List<Cluster> clusters;

    private ExecutorService manager;

    public ClusterManager() {
        clusters = new ArrayList<>();
        manager = Executors.newSingleThreadExecutor();
    }

    public void addCluster(Cluster cluster) {
        clusters.add(cluster);
    }

    private Cluster getAvailableResource(Job job) {
        Optional<Cluster> availableCluster = clusters.stream().filter(cluster -> cluster.getRam().get() >= job.getRam() && cluster.getCpu().get() >= job.getCpu()).findFirst();
        return availableCluster.orElse(null);
    }

    public Cluster allocateResource(Job job) {
        Cluster availableCluster = getAvailableResource(job);
        if (Objects.isNull(availableCluster)) {
            return null;
        }
        availableCluster.getCpu().addAndGet(-job.getCpu());
        availableCluster.getRam().addAndGet(-job.getRam());
        return availableCluster;
    }

}

@AllArgsConstructor
@Getter
@Setter
class Job {
    private Integer id;
    private Integer ram;
    private Integer cpu;
    private Long time;
    private Integer priority;
}

@Getter
@Setter
class JobSchedulerManager {

    private final ClusterManager clusterManager;

    private PriorityBlockingQueue<Job> q;

    private ExecutorService executorService;

    public JobSchedulerManager() {
        q = new PriorityBlockingQueue<>(10, (p, q) -> p.getPriority() - q.getPriority());
        clusterManager = new ClusterManager();
        executorService = Executors.newFixedThreadPool(3);
    }

    public void submitJob(Job job) throws InterruptedException {
        q.add(job);
    }

    public void addCluster(Cluster cluster) {
        clusterManager.getClusters().add(cluster);
    }

    public void startClusters() {
        executorService.submit(() -> {
            while (true) {
                Job job = q.take();
                Cluster availableCluster = null;
                while (true) {
                    availableCluster = clusterManager.allocateResource(job);
                    if (Objects.nonNull(availableCluster)) {
                        break;
                    }
                    Thread.sleep(100);
                }
                availableCluster.submit(job);
                System.out.println("Job_id: " + job.getId() + " running on cluster_id: " + availableCluster.getId());
            }
        });
    }
}


public class JobScheduler {

    public static void main(String[] args) throws InterruptedException {

        // Setup job scheduler
        JobSchedulerManager jobSchedulerManager = new JobSchedulerManager();
        jobSchedulerManager.addCluster(new Cluster(1, 32, 8));
        jobSchedulerManager.addCluster(new Cluster(2, 32, 8));
        jobSchedulerManager.addCluster(new Cluster(3, 64, 8));
        jobSchedulerManager.startClusters();

        // Submit a finite number of jobs for testing
        int jobCount = 20;  // Change this number to test more or fewer jobs
        Random random = new Random();

        for (int jobId = 1; jobId <= jobCount; jobId++) {
            int ram = random.nextInt(30) + 1;   // RAM between 1 and 30
            int cpu = random.nextInt(6) + 1;    // CPU between 1 and 6
            long time = 500L + random.nextInt(1000); // Execution time between 500ms and 1500ms
            int priority = random.nextInt(5);   // Priority between 0 and 4

            jobSchedulerManager.submitJob(new Job(jobId, ram, cpu, time, priority));
        }

        // Optional: Wait to ensure all jobs complete before shutdown (not mandatory in real systems)
        Thread.sleep(10000); // Let jobs run for a while
    }
}
