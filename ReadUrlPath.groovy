import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;

def Message extractUrlPathAndQuery(Message message) {

       //get url 
       def map = message.getHeaders();
       def url = map.get("CamelHttpPath");
       def queryString = map.get("CamelHttpQuery");

       //split url path
       String[] vUrl;
       vUrl = url.split('/');
       int size = vUrl.length;

       if (size==3) {
           message.setProperty("service", vUrl[size-3]);
           message.setProperty("resource", vUrl[size-2]);
           message.setProperty("id", vUrl[size-1]);
           message.setProperty("operation", 'read');
       } else if (size==2) {
           message.setProperty("service", vUrl[size-2]);
           message.setProperty("resource", vUrl[size-1]);
           message.setProperty("id", "all");
           message.setProperty("operation", 'query');
       } else {
           message.setProperty("id", "none");
           message.setProperty("operation", 'read');
       }
       
       //split url query
       if (queryString!=null) {
           String[] vQuery;
           vQuery = queryString.split('&');

       //set properties
           for( String pair : vQuery ) {
               String[] vPairs = pair.split('=')
               message.setProperty(vPairs[0].replace("\$",""), vPairs[1]);
           }
       }
       
       return message;
}