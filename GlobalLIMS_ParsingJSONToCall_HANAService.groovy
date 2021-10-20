import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def Message processData(Message message) 
{
    //get  response payload from the GlobalLIMS database
    def mResponseBatches = new JsonSlurper().parseText(message.getBody(String.class))
	
    //Initialize map to get the original Input Payload, its corresponding size and URI value from the Exchange Property
	def mRequestBatches = new JsonSlurper().parseText(message.getProperty("inPayload"))
    int requestBatchesSize = mRequestBatches.value.size()
    
    //Check if the response from the GlobalLIMS is empty
    if (mResponseBatches.SELECT_FROM_GLOBALLIMS_response.size() > 0)
        {
        //Intialize a Set to hold the field names and field values of the target message
        Set finalJsonSet = []

        //Creat object from scratch if json input only contains one batch set
	    if (!(mResponseBatches.SELECT_FROM_GLOBALLIMS_response.row.BATCHNO instanceof java.util.ArrayList)) {
            mResponseBatches = [SELECT_FROM_GLOBALLIMS_response:
                [row:[mResponseBatches.SELECT_FROM_GLOBALLIMS_response.row]
                ]
            ]   
        }

		//Loop through the JDBC response message and also through the input payload and then compare the batch Numbers
        //if Batch Number is a match then initialize values in a Set
        for (int inputBatchCount = 0; inputBatchCount < requestBatchesSize; inputBatchCount++)
        {
        def matchedBatchNumber = mResponseBatches.SELECT_FROM_GLOBALLIMS_response.row.find 
        {responseBatch -> responseBatch.BATCHNO == mRequestBatches.value[inputBatchCount].batchNumber}
        if(matchedBatchNumber)
            {
               finalJsonSet << [
								batch_batchNumber: matchedBatchNumber.BATCHNO,
								batch_plant: mRequestBatches.value[inputBatchCount].plant,
								batch_materialNumber: mRequestBatches.value[inputBatchCount].materialNumber, 
								batch_releaseType: mRequestBatches.value[inputBatchCount].releaseType,
								limsBatchStatus: matchedBatchNumber.BATCHSTATUS,
								limsURI: message.getProperty("URI_Format") + matchedBatchNumber.BATCHID,
								limsDispositionCode: matchedBatchNumber.DISPOSITIONCODE,
								limsBatchID: matchedBatchNumber.BATCHID
								] 
            }
        else
            {
                finalJsonSet << [
								    batch_batchNumber: mRequestBatches.value[inputBatchCount].batchNumber,
									batch_plant: mRequestBatches.value[inputBatchCount].plant,
									batch_materialNumber: mRequestBatches.value[inputBatchCount].materialNumber, 
									batch_releaseType: mRequestBatches.value[inputBatchCount].releaseType,
									limsBatchStatus: null,
									limsURI: null,
									limsDispositionCode: null,
									limsBatchID: "Not Found"
								]
            }    
        }

        //Convert the Set to JSON Format    
        finalJson = JsonOutput.toJson(finalJsonSet)
        message.setBody(finalJson)
        }
        else
            {
                String errMessage = "[{\"Error Response\":\"Batch Number " + mRequestBatches.value.batchNumber.toString() + " does not exist in GlobalLIMS database\"}]"
                //finalJson = JsonOutput.toJson(errMessage)
                message.setBody(errMessage)
            }
    return message
}