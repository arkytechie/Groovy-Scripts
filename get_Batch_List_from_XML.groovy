import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.util.XmlSlurper

def Message processData(Message message) 
{
    def xmlRequest = new XmlParser().parseText(message.getBody(String.class))
    def batchList = []

    for (int i = 0; i < xmlRequest.select_response.row.size(); i++) {
        batchList << xmlRequest.select_response.row[i].BATCHNUMBER.text()
    }
    //To remove the square brackets from the List
    def finalBatchList = "'" + batchList.join("','") + "'"
    message.setProperty ("batchList", finalBatchList)
	
    return message
}