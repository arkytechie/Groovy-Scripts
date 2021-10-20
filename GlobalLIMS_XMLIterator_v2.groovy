import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.util.XmlSlurper
import groovy.util.XmlParser
import groovy.xml.MarkupBuilder

def Message processData(Message message) {
    def body = message.getBody(String.class)
    def parsedXml = new XmlSlurper().parseText(body)
	def Val = parsedXml.value
    int valueSize = parsedXml.value.size()
    
	StringBuilder batchBuild = new StringBuilder()
	StringBuilder plantBuild = new StringBuilder()
	StringBuilder matnrBuild = new StringBuilder()

	for (int i = 0; i < valueSize; i++)
		{
			batchBuild.append("\'"+ Val[i].batchNumber.text() +"\'")
			plantBuild.append("\'"+ Val[i].plant.text() +"\'")
			matnrBuild.append("\'"+ Val[i].materialNumber.text() +"\'")
			
			if (i == (valueSize-1))
				break
			else
				{
					batchBuild.append(",")
					plantBuild.append(",")
					matnrBuild.append(",")
				}
		}
	/*
	formatXml = '{"batchNumbers":'+ '\"' +batchBuild + '\"' + '}' + ',' + '{"plant":'+ '\"' +plantBuild + '\"' + '}' + ',' + '{"materialNumbers":'+ '\"' +matnrBuild + '\"' + '}'
	message.setBody(formatXml)
	*/

    message.setProperty ("batchNumbers", batchBuild)
    message.setProperty ("Plants", plantBuild)
    message.setProperty ("MaterialNumbers", matnrBuild)
    return message
	
}