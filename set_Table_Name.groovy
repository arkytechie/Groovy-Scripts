import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def Message processData(Message message) 
{
    def mapHeader = message.getHeaders()
    def entityInScope = mapHeader.get("entityInScope")
    def mapProperty = message.getProperties()
    def inPayload = mapProperty.get("inputBody")
    def incomingPayload = new JsonSlurper().parseText(inPayload)
    def aRequestBatches = incomingPayload.value

    def tableName = "COM_NOVONORDISK_BATCHASSIGNMENT_"
    def tableNameSuffix = ""
    
	if (entityInScope == "Components") {
			tableNameSuffix = "BATCHCOMPONENTS"
    } else {
        tableNameSuffix = entityInScope.toUpperCase()
    }
    //Table Name Finalized
    message.setProperty ("hanaTableName", tableName+tableNameSuffix)
    
 
    aRequestBatches.any { 
                        requestBatch ->
                            StringBuilder primaryKeyValues = new StringBuilder()
                            StringBuilder keyValues = new StringBuilder()
                            requestBatch.each {
                                if (it.key)
                                keyValues.append(it.key.toUpperCase() + ",")
                            }    
                        primaryKeyValues.append (keyValues.substring(0, keyValues.length() - 1))
                        //println(primaryKeyValues)
                        message.setProperty ("incomingColumnNames", primaryKeyValues)
    }
	
	return message
}
