import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.util.XmlSlurper
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def Message processData(Message message) 
{
    //get  response payload from the GlobalLIMS database
    def mResponseBatches = new XmlSlurper().parseText(message.getBody(String.class))

    //Initialize map to get the original Input Payload and shortened path from the Exchange Property
	//def mRequestBatches = new XmlSlurper().parseText(message.getProperty("inputPayload"))

    def mRequestBatches = new XmlSlurper().parseText(message.getProperty("inputPayload"))
    //println(mRequestBatches.getClass())
    //println (aRequestBatches.row.getClass())

    //println(mResponseBatches.SELECT_FROM_GLOBALLIMS_response.row.size())
    if (mResponseBatches.SELECT_FROM_GLOBALLIMS_response.row.size() == 0) {
        //println("Response payload is empty")
        
        aResponseBatches = mResponseBatches.SELECT_FROM_GLOBALLIMS_response

        String formattedResponseBatches = """<?xml version="1.0" encoding="UTF-8" standalone="no"?>
            <NN_3593_GlobalLIMS>
                <SELECT_FROM_GLOBALLIMS_response>
                    <row></row>
                </SELECT_FROM_GLOBALLIMS_response>
            </NN_3593_GlobalLIMS>"""
        mResponseBatches = new XmlSlurper().parseText(formattedResponseBatches)
    }

    def reqRespMatchCount = 0
    def finalBatchList = []
    def reqCount, respCount = 0
    
    //println(mRequestBatches.row.size() + " , " + mResponseBatches.SELECT_FROM_GLOBALLIMS_response.row.size())

    for (reqCount = 0; reqCount < mRequestBatches.row.size(); reqCount++) {
        for (respCount = 0; respCount < mResponseBatches.SELECT_FROM_GLOBALLIMS_response.row.size(); respCount++) {
            //println ("check condition")
                if (mRequestBatches.row[reqCount].batch_batchNumber.text() == mResponseBatches.SELECT_FROM_GLOBALLIMS_response.row[respCount].BATCHNO.text()) {
                    println ("match found")

                     finalBatchList << [
                                batch_batchNumber: mResponseBatches.SELECT_FROM_GLOBALLIMS_response.row[respCount].BATCHNO.text(),
                                batch_plant: mRequestBatches.row[reqCount].batch_plant.text(),
                                batch_materialNumber: mRequestBatches.row[reqCount].batch_materialNumber.text(), 
                                batch_releaseType: mRequestBatches.row[reqCount].batch_releaseType.text(),
                                limsBatchStatus: mResponseBatches.SELECT_FROM_GLOBALLIMS_response.row[respCount].BATCHSTATUS.text(),
                                limsURI: message.getProperty("URI_Format") + mResponseBatches.SELECT_FROM_GLOBALLIMS_response.row[respCount].BATCHID.text(),
                                limsDispositionCode: mResponseBatches.SELECT_FROM_GLOBALLIMS_response.row[respCount].DISPOSITIONCODE.text(),
                                limsBatchID: mResponseBatches.SELECT_FROM_GLOBALLIMS_response.row[respCount].BATCHID.text()
                                ]
                    reqRespMatchCount += 1
                }
        }
        if (reqRespMatchCount == 0) {
                finalBatchList << [
                                    batch_batchNumber: mRequestBatches.row[reqCount].batch_batchNumber.text(),
                                    batch_plant: mRequestBatches.row[reqCount].batch_plant.text(),
                                    batch_materialNumber: mRequestBatches.row[reqCount].batch_materialNumber.text(), 
                                    batch_releaseType: mRequestBatches.row[reqCount].batch_releaseType.text(),
                                    limsBatchStatus: null,
                                    limsURI: null,
                                    limsDispositionCode: null,
                                    limsBatchID: "Not Found"
                ] 
        }

        else {
            reqRespMatchCount = 0
        }                    
    }

    def finalList = JsonOutput.toJson(finalBatchList)
    message.setBody(finalList)
    
    println (finalList)

    return message
}