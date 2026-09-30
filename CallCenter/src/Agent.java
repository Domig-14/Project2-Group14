import java.util.concurrent.ThreadLocalRandom;

public class Agent implements Runnable{
    private final int ID;

    public Agent(int ID) {
        this.ID = ID;
    }


    public void run() throws RuntimeException{
        int customerPerAgent = CallCenter.customersPerAgent;
        for (int i = 0; i < customerPerAgent; i++){

            try {
                CallCenter.servicePermit.acquire();
                help();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }

        }// close for loop

        System.out.println("\nAgent " + ID + " has finished!!!\n");
    }

    public void help() throws Exception{
        int customer;

        try {
            CallCenter.serviceLock.lock();

            customer = CallCenter.takeFromService();

            System.out.println("Agent " + ID + " starts serving customer " + customer);

            // simulate spending time serving a customer
            Thread.sleep(ThreadLocalRandom.current().nextInt(20, 200));

            System.out.println("ALERT: Agent " + ID + " finished serving customer " + customer);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            CallCenter.serviceLock.unlock();
            CallCenter.servicePermit.release();
        }

    }

}
