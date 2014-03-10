import java.util.HashMap;
import java.util.Map;


public class TestMap {
	
	public static void main(String[] args) {
		Map<String,String> map = new HashMap<String, String> ();
		map.put("hello", "world");
		map.put("hello", "will");
		
		System.out.println(map.size());
		System.out.println(map.get("hello"));
	}

}
