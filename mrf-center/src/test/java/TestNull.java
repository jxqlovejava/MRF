import java.util.Map;


public class TestNull {
	
	public static void main(String[] args) {
		Map map = null;
		if(map instanceof Map) {
			System.out.println(true);
		}
		else {
//			System.out.println(map);
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(null+"");
		System.out.println(sb.toString());
		
		System.out.println(",".substring(0, 0));
	}

}
