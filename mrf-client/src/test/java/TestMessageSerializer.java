import java.io.IOException;
import java.io.Serializable;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

import com.google.gson.Gson;
import com.nali.msg.message.NewMemberMsgMessage;


public class TestMessageSerializer {
	
	public SimpleMessageConverter simpleConverter;
	public JsonMessageConverter jsonConverter;
	
	public Message messageA;
	public Message messageB;
	public Message messageC;
	public Message messageD;
	
	public String messageAJsonStr;
	public String messageBJsonStr;
	public String messageCJsonStr;
	public String messageDJsonStr;
	
	public Message deserializedMessageA;
	public Message deserializedMessageB;
	public Message deserializedMessageC;
	public Message deserializedMessageD;
	
	public ObjectMapper objectMapper = new ObjectMapper();
	
	public Gson gson;
	
	@Before
	public void setUp() {
		// manual construct, default MessageProperties
		messageA = new Message("hello".getBytes(), new MessageProperties());
		
		// manual construct and manual construct MessageProperties
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setAppId("mrf");
		messageProperties.setMessageId("100010");
		messageB = new Message("world".getBytes(), messageProperties);
		
		// get message via SimpleMessageConverter
		simpleConverter = new SimpleMessageConverter();
		messageC = simpleConverter.toMessage(new Feed(1, "simple converter feed"), new MessageProperties());
		
		// get message via JsonMessageConverter
		jsonConverter = new JsonMessageConverter();
		messageD = jsonConverter.toMessage(new Feed(2, "json converter feed"), new MessageProperties());
		
		gson = new Gson();
	}
	
	@Test
	public void testDeserializeMessage() {
		messageAJsonStr = gson.toJson(messageA);
		System.out.println(messageAJsonStr);
		deserializedMessageA = gson.fromJson(messageAJsonStr, Message.class);
		System.out.println(deserializedMessageA);
		System.out.println(new String(deserializedMessageA.getBody()));
		
		System.out.println();
		
		messageBJsonStr = gson.toJson(messageB);
		System.out.println(messageBJsonStr);
		deserializedMessageB = gson.fromJson(messageBJsonStr, Message.class);
		System.out.println(deserializedMessageB);
		System.out.println(new String(deserializedMessageB.getBody()));

		System.out.println();
		
		// 必须指定领域对象类型
		messageC = simpleConverter.toMessage(new Feed(1, "simple converter feed"), new MessageProperties());
		Assert.assertTrue(messageC.getBody() != null && messageC.getBody().length > 0);
		messageCJsonStr = gson.toJson(messageC);
		System.out.println(messageCJsonStr);
		deserializedMessageC = gson.fromJson(messageCJsonStr, Message.class);
		System.out.println(deserializedMessageC);
		System.out.println(simpleConverter.fromMessage(deserializedMessageC));
		System.out.println(new String(deserializedMessageC.getBody()));
		
		System.out.println();
		
		Assert.assertTrue(messageD != null);
		messageDJsonStr = gson.toJson(messageD);
		System.out.println(messageDJsonStr);
		deserializedMessageD = gson.fromJson(messageDJsonStr, Message.class);
		System.out.println(deserializedMessageD);
		System.out.println(jsonConverter.fromMessage(deserializedMessageD));
		System.out.println(new String(deserializedMessageD.getBody()));
		
		// A、如果contentType为application/json，则直接new String(deserializedMessage.getBody())即可
		// C、如果contentType为application/x-java-serialized-object，则new String(deserializedMessage.getBody())
		//    得到的字符串带有乱码需要进一步处理
		// D、如果contentType为application/octet-stream则new String(deserializedMessage.getBody())得到的字符串
		//    可能可读，也可能不可读
		// E、其他的统一new String(deserializedMessage.getBody())即可
	}

	@After
	public void tearDown() {
		
	}
}

class Feed implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	
	private String content;
	
	public Feed() {
		
	}
	
	public Feed(int id, String content) {
		this.id = id;
		this.content = content;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "(id: \"" + this.id + "\", content: \"" + this.content + "\")"; 
	}
	
}
