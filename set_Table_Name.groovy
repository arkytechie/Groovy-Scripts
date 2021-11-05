import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def Message processData(Message message) 
{
    def entityInScope = mapHeader.get("entityInScope")

    def tableName = "COM_NOVONORDISK_BATCHASSIGNMENT_"
    def tableNameSuffix = ""
    
	if (entityInScope == "Components") {
			tableNameSuffix = "BATCHCOMPONENTS"
    } else {
        tableNameSuffix = entityInScope.toUpperCase()
    }
    //Table Name Finalized
    message.setProperty ("tableNameProperty", tableName+tableNameSuffix)
	
	return message
}
