package havayoluRezervasyon;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Metotlar implements Runnable {

	ReentrantLock lock;
	Condition condition;
	int userID;
	int istekBilet;
	int isleyisSirasi;
	String biletDurum;
	int[] bilet;

	public Metotlar(ReentrantLock lock, Condition condition, int userID, int istekBilet, int isleyisSirasi,
			int[] bilet) {
		this.lock = lock;
		this.condition = condition;
		this.userID = userID;
		this.istekBilet = istekBilet;
		this.isleyisSirasi = isleyisSirasi;
		this.bilet = bilet;
	}
	// true if current thread holds this lock and false otherwise
	// eger current thread locku hold etmiyorsa true

	private void makeReservation() {		//writer 
		lock.lock(); // write isleminde yalnizca bir threadin yazmasini istedigim icin lock metodunu kullaniyorum.
		try {
			if (!lock.isHeldByCurrentThread()) { // eger o anki(current) thread lock durumundaysa false degilse true	
				condition.await();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			System.out.println("Saat: " + java.time.LocalTime.now());
			System.out.println("Writer" + userID + "   " + (istekBilet + 1)
					+ " numaralý bileti alabilmek için istekte bulunuyor.....");
			if (bilet[istekBilet] == 0) { //eger bilet'in durumu 0 ise satin alinmamis demektir ve biletin durumu 1 e cekilir.
				bilet[istekBilet] = 1; 	//biletin durumu 1 oldugundan baska writer tarafindan alinamaz durumdadir.
				System.out.println("Writer" + userID + "  " + (istekBilet + 1)
						+ " numaralý bileti baþarýyla satýn aldýnýz.");
			} else {
				if (bilet[istekBilet] == 1) {	//biletin durumu 1 ise biletDurum degiskenine DOLU yaz
					biletDurum = "-- DOLU --";
				}					
				else {
					biletDurum = "-- BOS --"; 	//biletin durumu 0 ise biletDurum degiskenine BOS yaz
				}					
				System.out.println("Writer" + userID + "   " + (istekBilet + 1) + " numaralý bilet " +  biletDurum);
			}
			System.out.println("----------------------------------------");
			condition.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally { 	    // unlock cagirisini finally icerisinde kullanmamin sebebi calisma sirasinda bir istisna firlatilir
			lock.unlock(); //ve o thread henuz unlock yapmadan sonlanirsa durumunun onune gecmek icin yani istisna firlatilsa bile
		}				   // kilit serbest birakilmis olur.

	}

	private void ReaderThread() {  //queryReservation
		lock.lock();
		try {
			if (!lock.isHeldByCurrentThread()) { 									// Eger current readerthread lock sahipse false locka sahip degilse
				System.out.println("Writer isleminden oturu Bekletiliyorsunuz");	// true bc ! //true yapip conditon await ile current threadi beklet
				condition.await();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {

			System.out.println("Reader" + userID + " Bilet listesini görmek için istekte bulunuyor..... -------");
			for (int i = 0; i < bilet.length; i++) {
				System.out.println(i + "numaralý biletin durumu -> " + bilet[i]);
			}
			System.out.println("----------------------------------------");
			condition.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock(); // en sonda lock'u unlock etmeliyiz ki diger threadler erisebilsin.
		}

	}

	@Override
	public void run() {
			//durumlar
		try {
			if (isleyisSirasi == 0) { //once write sonra read islemi
				makeReservation();
				Thread.sleep(100);
				ReaderThread();
			} else if (isleyisSirasi == 1) { // once read sonra write islemi
				ReaderThread();
				 Thread.sleep(100);
				 makeReservation();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
