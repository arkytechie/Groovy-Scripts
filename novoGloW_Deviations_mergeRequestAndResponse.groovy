import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.util.XmlSlurper
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def Message processData(Message message) {
    
    //get  response payload from the novoGloW database
    def mResponseDeviations = new XmlSlurper().parseText(message.getBody(String.class))
    def aResponseDeviations = mResponseDeviations.SELECT_FROM_NDL_response
    
    def mRequestBatches = new XmlSlurper().parseText(message.getProperty("inputPayload"))
    def aRequestBatches = mRequestBatches.select_response

    def reqRespMatchCount = 0
    Set deviationJoinedSet = []
    def reqCount, respCount = 0
	
    for (reqCount = 0; reqCount < aRequestBatches.row.size(); reqCount++) {
        for (respCount = 0; respCount < aResponseDeviations.row.size(); respCount++) {
            if (aRequestBatches.row[reqCount].BATCHNUMBER.text() == aResponseDeviations.row[respCount].BATCH_NUMBER.text()){
                deviationJoinedSet << [
                                        batch_batchNumber: aResponseDeviations.row[respCount].BATCH_NUMBER.text(),
                                        batch_plant: aRequestBatches.row[reqCount].PLANT.text(),
                                        batch_materialNumber: aRequestBatches.row[reqCount].MATERIALNUMBER.text(), 
                                        batch_releaseType: aRequestBatches.row[reqCount].RELEASETYPE.text(),
                                        deviation: aResponseDeviations.row[respCount].DEVIATION_NUMBER.text(),
                                        phase: aResponseDeviations.row[respCount].PHASE.text(), 
                                        classification: aResponseDeviations.row[respCount].CLASSIFICATION.text(),
                                        dateOfOccurence: aResponseDeviations.row[respCount].OCCUR_DATE.text() ? aResponseDeviations.row[respCount].OCCUR_DATE.text().take(10) : null,
                                        deviationURI: message.getProperty("startURI") + aResponseDeviations.row[respCount].DEVIATION_ID.text(),
                                        headline: aResponseDeviations.row[respCount].HEADLINE.text()
                ]
            }
        }
    }

	message.setBody(JsonOutput.toJson(deviationJoinedSet))
    return message	

}