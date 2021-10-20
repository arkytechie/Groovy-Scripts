import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def Message processData(Message message) 
{
    //get  response payload from the GlobalLIMS database
    def mResponseBatches = new JsonSlurper().parseText(message.getBody(String.class))

    //Initialize map to get the original Input Payload and shortened path from the Exchange Property
	def mRequestBatches = new JsonSlurper().parseText(message.getProperty("inPayload"))
	def aRequestBatches = mRequestBatches.value
   
    //Check if the response from the GlobalLIMS is empty
    if (mResponseBatches.SELECT_FROM_GLOBALLIMS_response)
        {
        //Intialize a Set to hold the field names and field values of the target message
        Set finalJsonSet = []

        //define the path/shortened name for the response batches
        def aResponseBatches = mResponseBatches.SELECT_FROM_GLOBALLIMS_response.row
        
        //Creat object from scratch if json input only contains one batch set
	    if (!(aResponseBatches.BATCHNO instanceof java.util.ArrayList)) {
            mResponseBatches = [SELECT_FROM_GLOBALLIMS_response:
                [row:[aResponseBatches]
                ]
            ]   
        }

        //Initialize a counter that will keep track of the matched requestbatches to responseBatches
        //If a match is found the counter will increment by one each time and if not found it will remain zero
		def reqRespMatchCount = 0

		//Loop through the JDBC response message and also through the input payload and then compare the batch Numbers
        //if Batch Number is a match then initialize values in a Set

				aRequestBatches.each { requestBatch -> 
                        mResponseBatches.SELECT_FROM_GLOBALLIMS_response.row.each { 
							responseBatch ->
							if (requestBatch.batchNumber == responseBatch.BATCHNO) {
                                finalJsonSet << [
                                batch_batchNumber: responseBatch.BATCHNO,
                                batch_plant: requestBatch.plant,
                                batch_materialNumber: requestBatch.materialNumber, 
                                batch_releaseType: requestBatch.releaseType,
                                limsBatchStatus: responseBatch.BATCHSTATUS,
                                limsURI: message.getProperty("URI_Format") + responseBatch.BATCHID,
                                limsDispositionCode: responseBatch.DISPOSITIONCODE,
                                limsBatchID: responseBatch.BATCHID
                                ]
                            reqRespMatchCount += 1
                        }                     
                    } 
                    if (reqRespMatchCount == 0) {
                                finalJsonSet << [
                                    batch_batchNumber: requestBatch.batchNumber,
                                    batch_plant: requestBatch.plant,
                                    batch_materialNumber: requestBatch.materialNumber, 
                                    batch_releaseType: requestBatch.releaseType,
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
        //Convert the Set to JSON Format    
        finalJson = JsonOutput.prettyPrint(JsonOutput.toJson(finalJsonSet))
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