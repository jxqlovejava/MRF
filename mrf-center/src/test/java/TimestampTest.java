import java.sql.Timestamp;
import java.util.Date;

public class TimestampTest {
	
	public static void main(String[] args) {
		long curTimeMillis = System.currentTimeMillis();
		Timestamp ts1 = new Timestamp(curTimeMillis);
		Timestamp ts2 = new Timestamp(curTimeMillis + 3000);
		
		System.out.println(curTimeMillis);
		System.out.println(ts1.getTime());
		System.out.println(new Date().getTime());
		
//		System.out.println(ts1);
//		System.out.println(ts2);
	}

}
