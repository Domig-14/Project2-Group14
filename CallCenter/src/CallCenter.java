import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CallCenter {
    public final static int totalCustomers = 30;
    public final static int totalAgents = 3;
    public final static int customersPerAgent = 10;
    // shared data

    // arrival queue
    final static Queue<Integer> queue = new LinkedList<>();
    final static Queue<Integer> serviceQueue = new LinkedList<>();
    final static  ReentrantLock arrivalLock = new ReentrantLock();
    final static  ReentrantLock serviceLock = new ReentrantLock();
    final static Condition serviceNotEmpty = serviceLock.newCondition();
    final static Condition arrivalNotEmpty = arrivalLock.newCondition();

    public static final Semaphore servicePermit = new Semaphore(1);

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
        }
        finally{
            arrivalLock.unlock();
        }
        return customerID;
    }

    public static void addToService(int customerID){
        serviceLock.lock();
        try{
            serviceQueue.add(customerID);
            serviceNotEmpty.signal();
        }
        catch (Exception e){
            System.out.println("ERROR: Customer " + customerID + " was unable to be placed into the Service queue");
        }
        finally{
            serviceLock.unlock();
        }
        System.out.println("Customer " + customerID + " has been placed into the service queue ");
    }

    public static int takeFromService() throws Exception{
        int customerID;
        while (serviceQueue.isEmpty()) {

            serviceNotEmpty.await();
        }
        customerID = serviceQueue.remove();
        System.out.println("ALERT: Customer " + customerID + " has been removed from service queue");

        return customerID;
    }

    public static void main(String[] args) throws InterruptedException{
        // for long-lived tasks
        ExecutorService agentPool = Executors.newFixedThreadPool(4);

        //for short-lived, come-and-go tasks
        ExecutorService customerPool = Executors.newCachedThreadPool();

        // for the annoying Greeter
        ExecutorService greeterPool = Executors.newCachedThreadPool();


        greeterPool.submit(new Greeter());

        for(int i = 1; i <= totalAgents; i++){
            agentPool.submit(new Agent(i));
        }

        for(int i = 1; i <= totalCustomers; i++){
            customerPool.submit(new Customer(i));
            Thread.sleep(ThreadLocalRandom.current().nextInt(10, 100));
        }

        greeterPool.shutdown();
        greeterPool.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("\nGREETER TERMINATED\n");

        agentPool.shutdown();
        agentPool.awaitTermination(60, TimeUnit.SECONDS);
        System.out.println("\nAGENTS GOING HOME\n");

        customerPool.shutdown();



    }
}
