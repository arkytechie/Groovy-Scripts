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

	println (finalJsonSet)

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

    println (xmlSet)

    //def setCounter = 0
    /*
    //Request Message fetched from systems has an extra value as compared to the HANA DB so it needs to be inserted into the HANA DB
    if (finalJsonSet.size() > xmlSet.size()) {

        crudOperation = "INSERT"
    }
    //There is an additional value in the HANA DB that is extra as compared to input payload so that has to be removed from DB
    else if (finalJsonSet.size() < xmlSet.size()) {
            
            crudOperation = "DELETE"
    }
    //Both the set counts are equal so the loop can be made on any set count 
    else {
        for (setCounter = 0; setCounter < finalJsonSet.size(); setCounter++) {
            if (finalJsonSet[setCounter] != xmlSet[setCounter]) {
                diffCounter += 1
                diffSet << [xmlSet[setCounter]]
                crudOperation = "UPSERT"
            }
        }
    }
    //finJson.size ()
    finalJsonSet.each { finJson ->
                            xmlSet.each {
                                xmlResp ->
                                if (finJson.batch_batchNumber+finJson.batch_Plant+finJson.batch_materialNumber+finJson.batch_releaseType != 
                                xmlResp.batch_batchNumber+xmlResp.batch_Plant+xmlResp.batch_materialNumber+xmlResp.batch_releaseType) {
                                    diffCounter += 1
                                    
                                   
                                }
                                
                        }

    }

    finalJsonSet.each { finJson ->
        finJson.each { k1,v1 ->
            xmlSet.each { xmlResp ->
                            def v2 = xmlResp[k1]
                            if(v1!=v2) println "the value of $k1 in list1 `$v1` is "
                            else println "both are equal sets"
            }

}}



if (xmlSet.size() > finalJsonSet.size()) {
        xmlSet.each { xmlResp ->
                finalJsonSet.any {
                                    finJson ->
                                    if (xmlResp.batch_batchNumber+xmlResp.batch_Plant+xmlResp.batch_materialNumber+xmlResp.batch_releaseType !=
                                        finJson.batch_batchNumber+finJson.batch_Plant+finJson.batch_materialNumber+finJson.batch_releaseType) {
                                            println ("xml response: " + xmlResp)
                                            println ("final json set: " + finJson)

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
}
*/

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
    message.setProperty ("differenceSet", diffSet)
    message.setProperty ("crudOperation", crudOperation)

    return message
}
