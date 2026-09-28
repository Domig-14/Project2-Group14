public class Customer implements Runnable{
    private final int ID;

    public Customer(int ID) {
        this.ID = ID;
    }


    public void run(){
        CallCenter.addToArrival(ID);
        System.out.println("Customer " + ID + " enters the arrival queue");
    }
}
