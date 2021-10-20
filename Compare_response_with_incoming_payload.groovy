import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.util.*
import java.util.HashMap

def Message processData(Message message) 
{
	def mapProperty = message.getProperties()
	def inPayload = mapProperty.get("incomingPayload")
	def mRequestBatches = new JsonSlurper().parseText(inPayload)
	def aRequestBatches = mRequestBatches.value
	def requestBatchSize = aRequestBatches.size()
	def xmlResponse = new XmlParser().parseText(message.getBody(String.class))

	def totalCountOfResponses = 0
	def aXmlResponse = xmlResponse.'multimap:Message1'
	def responseCount
	
	for (responseCount = 0; responseCount < aXmlResponse.Root.size(); responseCount++) {
		totalCountOfResponses = totalCountOfResponses + (aXmlResponse.Root[responseCount].Statement_response.sql_query_count.text() as int)
	}
	
	if (requestBatchSize != totalCountOfResponses) {
		//Message Log Factory
       def messageLog = messageLogFactory.getMessageLog(message)
    
    	// Log the message
        //messageLog.addAttachmentAsString("http.ResponseBody", ex.getResponseBody(), "text/plain");

       messageLog.addAttachmentAsString(mapProperty["entityInScope"] +"-"+ mapProperty["CamelSplitIndex"] + "-Request", inPayload, "text/plain")
       messageLog.addAttachmentAsString(mapProperty["entityInScope"] +"-"+ mapProperty["CamelSplitIndex"] + "-Response", xmlResponse, "text/plain")
       
       //Throw exception
       throw new Exception("Response Count error:" + requestBatchSize + " != " + aXmlResponse.Root.size())

	}
	return message
}
