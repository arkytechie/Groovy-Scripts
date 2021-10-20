import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def Message processData(Message message) 
{
    def mRequestBatches = new JsonSlurper().parseText(message.getBody(String.class))
	def mapProperty = message.getProperties()

    def rootStart = "<NN_TEST_HANADB>\n"
    def rootEnd = "</NN_TEST_HANADB>"
    def frontendXmlString = "<Statement>\n<Batches action=\"SQL_QUERY\">\n<access>\n"
    def tailendXmlString = "\n</access>\n</Batches>\n</Statement>"

	StringBuilder outputMessage = new StringBuilder()
	outputMessage.append(rootStart)
    StringBuilder xmlQueryBuilder = new StringBuilder()
	xmlQueryBuilder.append(frontendXmlString)
	def query = "SELECT ID FROM COM_NOVONORDISK_BATCHASSIGNMENT_VERSIONS WHERE IsActive = 'true' "
	xmlQueryBuilder.append(query+tailendXmlString) 
    outputMessage.append(xmlQueryBuilder+"\n")
	
    message.setBody(outputMessage.append(rootEnd).toString())
    return message
}