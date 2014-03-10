public class TestInheritMethod {
	
	public static void main(String[] args) {
		Base sa = new SubA();
		sa.f();
		
		Base sb = new SubB();
		sb.f();
	}
	
}

abstract class Base {
	
	protected void f() {
		System.out.println("Base.f()");
	}
	
}

class SubA extends Base {
	
	@Override
	public void f() {
		System.out.println("SubA.f()");
		super.f();
	}
	
}

class SubB extends Base {
	
	@Override
	public void f() {
		System.out.println("SubB.f()");
	}
	
}

