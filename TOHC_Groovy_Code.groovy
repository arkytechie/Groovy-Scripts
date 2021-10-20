/**
 * [Default Generated Function; Performs a join between the initial request message and novoGloW response, and stores it in the body.
 * @param  {Message} message [The message being transferred]
 */
import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.util.XmlSlurper
import groovy.json.JsonBuilder

def Message processData(Message message) {
    
    def deviations = message.getBody(String.class)
	def deviationsList = new XmlSlurper().parseText(deviations)
	def reqMap = new JsonSlurper().parseText(message.getProperty("inboundBody"))
	
		Set joinedSet = []

	reqMap.value.each{requested -> deviationsList.'**'
	    .findAll{(it.BATCH_NUMBER == requested.batchNumber)}
	    .each{found ->
			joinedSet << [
    			batch_batchNumber: requested.batchNumber
    			,batch_materialNumber: requested.materialNumber
    			,batch_plant: requested.plant
    			,batch_releaseType: requested.releaseType
    			,deviation: found.DEVIATION_NUMBER.toString()
    			,phase: found.PHASE.toString()
    			,classification: found.CLASSIFICATION.toString()
    			,dateOfOccurrence: (found.OCCUR_DATE.toString()) ? found.OCCUR_DATE.toString().take(10) : null
    			,deviationURI: message.getProperty("startURI") + found.DEVIATION_ID.toString() + message.getProperty("endURI")
    			,headline: found.HEADLINE.toString()
			]
	    }
	}
	
	message.setBody(JsonOutput.toJson(joinedSet))
	
    return message
}

def Message fixJsonBeforeGather(Message message) {
    
    def res = message.getBody(String.class)
    
    //remove emmty bodies
    res = res.replace('[]','').trim()

	// Is not Last
    if(res.length()>0){
        res = res.substring(res.indexOf("[")+1).trim()
	    res = res.substring(0,res.lastIndexOf("]")).trim() + ","
	}
    
    message.setBody(res)
    return message
}

def Message fixJsonAfterGather(Message message) {
    
    def res = message.getBody(String.class)

	// Is not Last
    if(res.length()>0 && res.lastIndexOf(",") == res.length()-1){
	    res = "[" + res.substring(0,res.lastIndexOf(",")).trim() + "]"
	}
    
    message.setBody(res)
    return message
}


