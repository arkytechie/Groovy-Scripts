import com.sap.gateway.ip.core.customdev.util.Message;
import java.util.HashMap;
import groovy.xml.XmlUtil;
import groovy.util.XmlSlurper;
import groovy.util.slurpersupport.GPathResult;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput;
import groovy.json.JsonBuilder
    
    def Message processData(Message message) {

       def fieldname     
       def map = message.getProperties(); 
       
       def Jsoncontent = message.getBody(java.lang.String)
       def jsonParser = new JsonSlurper()
       def jsonObject = jsonParser.parseText(Jsoncontent)
 
       def payload = map.get("payload") 
	   GPathResult payloadString = new XmlSlurper().parseText(payload.toString())

       customtags = payloadString.CustomTags
   
       customtags.tag.each { 
           fieldname = it.name.toString()
           fieldvalue = it.value.toString()
           jsonObject.tags["$fieldname"] = fieldvalue
       }   
      
       def mergedJsonString = JsonOutput.toJson(jsonObject)
       message.setBody(mergedJsonString) 
       return message;

    }