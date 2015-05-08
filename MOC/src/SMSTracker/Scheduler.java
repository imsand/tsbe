package SMSTracker;

import java.lang.annotation.Repeatable;
import java.util.Timer;

public class Scheduler {

	public static void main(String[] args) {
		
		DBController db = new DBController();

		Communication runnable = new Communication("AT$GPSACP\r\n");
		
		//Timer t = new Timer();
		//t.schedule(runnable, 8000, 8000);
		
		//do {
			//new Thread(runnable).start();
			//try {
			//	Thread.sleep(10000);
			//} catch(InterruptedException e) { }
			//} while (db.y <= 3);
		
		new Thread(runnable).start();
		try {
				Thread.sleep(10000);
			} catch(InterruptedException e) { }
		new Thread(runnable).start();
	}

}
