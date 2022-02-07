import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;

def Message processData(Message message) {
 
    //Properties 
       def map = message.getProperties();
       def mapH = message.getHeaders();
       def ex = map.get("CamelExceptionCaught");
       def body = message.getBody(java.lang.String);
       def exbody = "";
      
       
    //Log keys in as custom log setHeader
    def messageLog = messageLogFactory.getMessageLog(message);
    
    //Set status getHeaders
    message.setProperty('SAP_MessageProcessingLogCustomStatus','FAILED');
    message.setHeader('CamelHttpResponseCode',500);
    
    //Log Payload
    messageLog.setStringProperty("ExceptionHandler", "Log Body")
    messageLog.addAttachmentAsString("Payload", body, "text/plain");
       
    if(ex!=null){
        messageLog.setStringProperty("ExceptionHandler", "Extracted from CamelExceptionCaught")
        if (ex.metaClass.respondsTo(ex, "getResponseBody")){
            //when exception has a response body
            exbody = ex.getResponseBody();
            exbody = exbody.replaceAll("[\\r\\n\\t]", "");
            exbody = exbody.replaceAll("[\"]", "'");
            messageLog.addAttachmentAsString("ResponseBody", exbody, "text/plain");

        } else {
            //else build json
            exbody = ex.getMessage();
            exbody = exbody.replaceAll("[\\r\\n\\t]", "");
            exbody = exbody.replaceAll("[\"]", "'");
            messageLog.addAttachmentAsString("ExceptionBody", exbody, "text/plain");
        
        }

    // body
    } else {
        exbody = body.replaceAll("[\\r\\n\\t\"]", "");
        exbody = exbody.replaceAll("[\"]", "'");
    } 
   
   // Throw exception
    throw new Exception(exbody);    
    
    return message;

}