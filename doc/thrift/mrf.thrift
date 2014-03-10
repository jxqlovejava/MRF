namespace java com.nali.mrfcore.thrift

/**
 * QueueConfig
 */
struct QueueConfig {
   1:string queueName,
   2:string host,
   3:i32 port,
   4:string username,
   5:string password
}

/**
 * RetryMessage
 */
struct RetryMessage {
   1:i64 msgID,
   2:string retryQueueName,
   3:string businessMsg,
   4:i64 retryInterval,
   5:i32 retriedTimes,
   6:i32 maxRetryTimes,
   7:i32 messageFlag,
   8:bool isFromRecover 
}

service MRFService {

   /**
    * register retry service and return exception queue's config
    */
   QueueConfig registerRetryService(1:string host, 2:i32 port, 3:string username, 4:string password, 
   			5:string retryQueueName, 6:string clientServiceName);
   
   /**
    * send retry message to mrf center for processing
    */
   void process(1:RetryMessage retryMessage);
   
}