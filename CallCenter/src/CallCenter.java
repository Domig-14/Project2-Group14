import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CallCenter {
    public final static int totalCustomers = 30;
    public final static int totalAgents = 3;
    // shared data

    // arrival queue
    private final static Queue<Integer> queue = new LinkedList<>();
    private final static Queue<Integer> serviceQueue = new LinkedList<>();
    private final static ReentrantLock arrivalLock = new ReentrantLock();
    private final static Condition arrivalNotEmpty = arrivalLock.newCondition();

    public static void addToArrival(int customerID){
        arrivalLock.lock();
        try {
            //critical section
            queue.add(customerID);
            arrivalNotEmpty.signal();
        }
        finally {
            arrivalLock.unlock();
        }
    }

    public static int takeFromArrival() throws Exception{
        int customerID;
        arrivalLock.lock();
        try {
            while (queue.isEmpty()) {

                arrivalNotEmpty.await();
            }
            customerID = queue.remove();
        }finally{
            arrivalLock.unlock();
        }
        return customerID;

    }

    public static void main(String[] args) throws InterruptedException{
        // for long-lived tasks
        ExecutorService agentPool = Executors.newFixedThreadPool(4);

        //for short-lived, come-and-go tasks
        ExecutorService customerPool = Executors.newCachedThreadPool();

        for(int i = 1; i <= totalAgents; i++){
            agentPool.submit(new Agent(i));
        }

        for(int i = 1; i <= totalCustomers; i++){
            customerPool.submit(new Customer(i));
            Thread.sleep(ThreadLocalRandom.current().nextInt(10, 100));
        }
        agentPool.shutdown();
        customerPool.shutdown();
    }
}
