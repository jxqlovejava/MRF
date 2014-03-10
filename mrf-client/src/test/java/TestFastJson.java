import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;


public class TestFastJson {
	
	@Test
	public void testSerialize() {
		JsonMessageConverter jsonMessageConverter = new JsonMessageConverter();
		Message originalMessage = jsonMessageConverter.toMessage(new DomainObject(), new MessageProperties());
		
		String serializedStr = JSON.toJSONString(originalMessage);
		System.out.println(serializedStr);
		Assert.isTrue(serializedStr != null && !serializedStr.isEmpty());
		
		Message deserializedMessage = JSON.parseObject(serializedStr, Message.class);
		System.out.println(deserializedMessage);
		Assert.notNull(deserializedMessage);
	}
	

}

class DomainObject {
	
	private int id;
	private String username;
	
	public DomainObject() {
		
	}
	
	public DomainObject(int id, String username) {
		this.id = id;
		this.username = username;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
