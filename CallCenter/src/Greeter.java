import java.util.concurrent.ThreadLocalRandom;

public class Greeter implements Runnable{

    public void run(){
        int customerID;
        for(int i = 0; i < CallCenter.totalCustomers; i++){
            try{
                customerID = CallCenter.takeFromArrival();
                Thread.sleep(ThreadLocalRandom.current().nextInt(20 , 200));

                //CallCenter.addToService(customerID);

                // Can reword message later just for placement
                System.out.println("Customer " + customerID + " has been placed into the service queue");


            } catch (Exception e){
                e.printStackTrace();
            }
        }// close for loop
    }
}
