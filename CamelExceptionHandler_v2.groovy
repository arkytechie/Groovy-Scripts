/*
Extract Exception information to be returned to the consumer
*/
import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
def Message processData(Message message) {
      
    //Properties 
       map = message.getProperties();
       ex = map.get("CamelExceptionCaught");
       
    //make use is HTTP !=200
        message.setHeader("CamelHttpResponseCode",500)    
    
     
       if(ex!=null){
          if (ex.metaClass.respondsTo(ex, "getResponseBody")){
            //when exception has a response body
            message.setBody(ex.getResponseBody());    

          } else {
            //else build json
            message.setBody('{"responseStatus": "FAILURE","error":"'+ex.getMessage().replaceAll("[^A-Za-z0-9()\\[\\]]", " ")+'"}');
            message.setHeader("Content-Type","application/json");
          }
       
      //Message Log Factory
       def messageLog = messageLogFactory.getMessageLog(message)    
      // Log the message
       messageLog.setStringProperty("Logging", "Payload as Attachment")
       messageLog.addAttachmentAsString("Error Response", message.getBody(), "text/plain");
       messageLog.addAttachmentAsString("Input Payload", map["inputPayload"], "text/plain");
       } 
       
       return message;

}

/*
Log body
*/
def Message logNow(Message message) {
      
    //Properties 
      map = message.getProperties();
      
      def payload = message.getBody(java.lang.String);
      def payload_size = payload.getBytes().length;
       
     //Message Log Factory
       def messageLog = messageLogFactory.getMessageLog(message)    
      // Log the message
       messageLog.addAttachmentAsString("body-"+map["CamelSplitIndex"]+"-"+payload_size,payload, "text/plain");
          
       return message;

}

/*
    Count Records in ChangeSet
*/
def Message checkRecordCount(Message message) {

	//Parse body using JsonSlurper and format it into Json
	def body = message.getBody(String.class);
	
    //Properties 
	def property = message.getProperties();
    def key = property['operationMethod'] + " " + property['entityInScope']
    //def count = body.findAll(key).size
    def count = body.split(key).length - 1
    
    //control record count
    message.setProperty('controlRecordCount', count);
    message.setProperty('controlPayload', body);
        
	return message;
  }

/*
    Check Atomicity Groups and log.
*/
def Message checkGroupCount(Message message) {

 def response = message.getBody(java.lang.String);
    def property = message.getProperties();
    def key = '\"status\":2'; //HTTP 200-299
    def count = response.split(key).length - 1
    
    //control record count
    message.setProperty('controlAtomicityGroup', count)
    
    if (property['controlRecordCount'] != property['controlAtomicityGroup']){
    
       //Message Log Factory
       def messageLog = messageLogFactory.getMessageLog(message)    
    
        // Log the message
       messageLog.addAttachmentAsString(property["entityInScope"] +"-"+ property["CamelSplitIndex"] + "-Request", property["controlPayload"], "text/plain");
       messageLog.addAttachmentAsString(property["entityInScope"] +"-"+ property["CamelSplitIndex"] + "-Response", response, "text/plain");
       
       //Throw exception
       throw new Exception('Control Record Count error:' + property['controlRecordCount'] + ' != ' + property['controlAtomicityGroup']);
    
    }
    
    
    return message;
}