import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def Message processData(Message message) 
{
    def mRequestBatches = new JsonSlurper().parseText(message.getBody(String.class))
	def mapProperty = message.getProperties()
	def tableName = mapProperty.get("tableNameProperty")
	message.setProperty ("mainIncomingPayload", message.getBody(String.class))

    def rootStart = "<NN_TEST_HANADB>\n"
    def rootEnd = "</NN_TEST_HANADB>"
    def frontendXmlString = "<Statement>\n<Batches action=\"SQL_QUERY\">\n<access>\n"
    def tailendXmlString = "\n</access>\n</Batches>\n</Statement>"

	StringBuilder outputMessage = new StringBuilder()
	outputMessage.append(rootStart)
    StringBuilder xmlQueryBuilder = new StringBuilder()
	xmlQueryBuilder.append(frontendXmlString)
	//SELECT * FROM SYS.TABLE_COLUMNS WHERE TABLE_NAME = 'MAKT' ORDER BY POSITION;
	def query = "SELECT COLUMN_NAME FROM SYS.TABLE_COLUMNS WHERE TABLE_NAME = '" + tableName + "' AND COLUMN_NAME LIKE '%VERSION%' "
	xmlQueryBuilder.append(query+tailendXmlString) 
    outputMessage.append(xmlQueryBuilder+"\n")
	
    message.setBody(outputMessage.append(rootEnd).toString())
    return message
}