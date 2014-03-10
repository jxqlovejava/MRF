

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TestReflect {
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		MyClass m = new MyClass(1, "hello");
		Field[] fs = MyClass.class.getDeclaredFields();
//		System.out.println(fs.length);
		for(int i = 0; i < fs.length; i++) {
			Field f = fs[i];
			if(Modifier.isPrivate(f.getModifiers()) && f.getName() == "name") {
				f.setAccessible(true);
				System.out.println(f.get(m));
			}
		}
		
		Field[] fs2 = MyClassB.class.getDeclaredFields();
		for(Field f: fs2) {
			System.out.println(f.getType().getName().toLowerCase().contains("boolean"));
		}
	}

}

class MyClassB {
	private boolean a;
	private Boolean b;
}

class MyClass {
	
	private int i;
	
	private String name;
	
	public MyClass() {
		
	}
	
	public MyClass(int i, String name) {
		this.i = i;
		this.name = name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setI(int i) {
		this.i = i;
	}
	
	public String getName() {
		return name;
	}
	
}
