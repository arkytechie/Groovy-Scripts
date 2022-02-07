import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def Message processData(Message message) 
{
    def mRequestBatches = new JsonSlurper().parseText(message.getProperty("inputBody"))
    def aRequestBatches = mRequestBatches.value
    
    Set finalJsonSet = []
                
    aRequestBatches.each { requestBatch -> 
                                finalJsonSet << [
                                    "batch_batchNumber" : requestBatch.batch_batchNumber,
                                    "batch_Plant" : requestBatch.batch_plant,
                                    "batch_materialNumber" : requestBatch.batch_materialNumber,
                                    "batch_releaseType" : requestBatch.batch_releaseType
                                ]                                                
    }

	//println (finalJsonSet)

    def xmlResponse = new XmlParser().parseText(message.getBody(String.class))

	def totalCountOfResponses = 0
	def aXmlResponse = xmlResponse.'multimap:Message1'
	def responseCounter

    Set xmlSet = []

    for (responseCounter = 0; responseCounter < aXmlResponse.Root.size(); responseCounter++) {
		xmlSet << [
            "batch_batchNumber" : aXmlResponse.Root[responseCounter].Statement_response.row.BATCH_BATCHNUMBER.text(),
            "batch_Plant" : aXmlResponse.Root[responseCounter].Statement_response.row.BATCH_PLANT.text(),
            "batch_materialNumber" : aXmlResponse.Root[responseCounter].Statement_response.row.BATCH_MATERIALNUMBER.text(),
            "batch_releaseType" : aXmlResponse.Root[responseCounter].Statement_response.row.BATCH_RELEASETYPE.text()
        ]
	}

    //println (xmlSet)

def crudOperation = "NONE"
def diffCounter = 0
Set diffSet = []
if (xmlSet.size() > finalJsonSet.size()) {
        crudOperation = "DELETE" 
        xmlSet.each { xmlResp ->
                def foundMatching = finalJsonSet.any { finJson ->
                    xmlResp.batch_batchNumber + xmlResp.batch_Plant + xmlResp.batch_materialNumber + xmlResp.batch_releaseType == 
                    finJson.batch_batchNumber + finJson.batch_Plant + finJson.batch_materialNumber + finJson.batch_releaseType
                }
                    if (!foundMatching) {
                        diffCounter += 1
                        diffSet << [
                                    "batch_batchNumber" : xmlResp.batch_batchNumber,
                                    "batch_Plant" : xmlResp.batch_Plant,
                                    "batch_materialNumber" : xmlResp.batch_materialNumber,
                                    "batch_releaseType" : xmlResp.batch_releaseType
                        ]
                    }
        }

}

if (xmlSet.size() < finalJsonSet.size()) {
        crudOperation = "UPSERT"
        finalJsonSet.each { finJson ->
                def foundMatching = xmlSet.any { xmlResp ->
                    xmlResp.batch_batchNumber + xmlResp.batch_Plant + xmlResp.batch_materialNumber + xmlResp.batch_releaseType == 
                    finJson.batch_batchNumber + finJson.batch_Plant + finJson.batch_materialNumber + finJson.batch_releaseType
                }
                    if (!foundMatching) {
                        diffCounter += 1
                        diffSet << [
                                    "batch_batchNumber" : finJson.batch_batchNumber,
                                    "batch_Plant" : finJson.batch_Plant,
                                    "batch_materialNumber" : finJson.batch_materialNumber,
                                    "batch_releaseType" : finJson.batch_releaseType
                        ]
                    }
        }
}

    message.setProperty ("differenceCount", diffCounter)
    message.setProperty ("differenceSet", JsonOutput.toJson(diffSet))
    message.setProperty ("crudOperation", crudOperation)

    return message
}
