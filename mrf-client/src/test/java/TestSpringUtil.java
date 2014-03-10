import java.util.Set;

import org.springframework.util.ClassUtils;

import com.nali.mrfclient.service.MRFClientServiceStarter;


public class TestSpringUtil {
	
	public static void main(String[] args) {
		Set<Class> clsSet = ClassUtils.getAllInterfacesForClassAsSet(MRFClientServiceStarter.class);
		for (Class cls : clsSet) {
			System.out.println(cls.getName() + "\t" + cls.getSimpleName());
		}
	}

}
