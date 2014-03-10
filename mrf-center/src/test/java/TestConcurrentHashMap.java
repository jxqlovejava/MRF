import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;


public class TestConcurrentHashMap {
	
	public static void main(String[] args) {
		Map<String, String> map = new ConcurrentHashMap<String, String> ();
		System.out.println(map.remove("name"));
	}

}
