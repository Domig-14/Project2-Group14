import java.util.concurrent.ThreadLocalRandom;

public class Greeter implements Runnable{

    public void run() throws RuntimeException{
        for(int i = 1; i <= CallCenter.totalCustomers; i++){
            try{
                greet();

            } catch (Exception e){
                e.printStackTrace();
            }
        }//close for loop
        System.out.println("Greeter has finished");
    }

    public void greet() throws Exception {
        int customer;
        customer = CallCenter.takeFromArrival();

        Thread.sleep(ThreadLocalRandom.current().nextInt(20 , 200));

        CallCenter.addToService(customer);
    }
}
