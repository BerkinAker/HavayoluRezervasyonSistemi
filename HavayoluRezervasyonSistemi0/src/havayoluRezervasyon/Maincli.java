package havayoluRezervasyon;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;


public class Maincli {

	public static void main(String[] args) throws InterruptedException {
		
		  	ReentrantLock lock = new ReentrantLock(true);
	        Condition condition = lock.newCondition();
	        int istekBilet = 0;
	        int [] bilet = new int[5];
	        //Verilen 5. parametre kullanicinin once hangi islemi yapmasina karar veriyor 
	        //ornegin 0 ise once write(makereservation) sonra read(queryReservation) islemi
	        //1 ise once read sonra write(makereservation) islemi
	        Metotlar kullanici1 = new Metotlar(lock, condition, 1,istekBilet, 0,bilet);
	        Metotlar kullanici2 = new Metotlar(lock, condition, 2,istekBilet, 1, bilet);
	        Metotlar kullanici3 = new Metotlar(lock, condition, 3,istekBilet, 1, bilet);
	        
	        Thread thread1 = new Thread(kullanici1);
	        Thread thread2 = new Thread(kullanici2);
	        Thread thread3 = new Thread(kullanici3);
	        
	        
	        thread1.start();
	        thread2.start();
	        thread3.start();
	        thread1.join();
	        thread2.join();
	        thread3.join();
	}

}
