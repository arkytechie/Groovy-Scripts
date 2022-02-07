import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.util.XmlSlurper
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import com.sap.it.api.ITApiFactory


def Message processData(Message message) 
{
    def mRequestBatches = new JsonSlurper().parseText(message.getProperty("inputBody"))
    def xmlRespList = new XmlParser().parseText(message.getBody(String.class))
	def crudOperation = "NONE"
    def diffSetCount = 0
    Set diffSet = []

    if (xmlRespList.'multimap:Message1'.Root.size() > mRequestBatches.value.size()) {
            crudOperation = "DELETE"
		    xmlRespList.'multimap:Message1'.Root.Statement_response.row.each { response ->  
					def matchFound =  mRequestBatches.value.any { 
                        it.batch_batchNumber + it.batch_plant + it.batch_materialNumber + it.batch_releaseType == 
                        response.BATCH_BATCHNUMBER.text() + response.BATCH_PLANT.text() + response.BATCH_MATERIALNUMBER.text() + response.BATCH_RELEASETYPE.text()
					}
            		if (!matchFound){
                        diffSetCount += 1
                        diffSet << [
                                    "BATCH_BATCHNUMBER": response.BATCH_BATCHNUMBER.text(),
                                    "BATCH_PLANT": response.BATCH_PLANT.text(),
                                    "BATCH_MATERIALNUMBER": response.BATCH_MATERIALNUMBER.text(),
                                    "BATCH_RELEASETYPE": response.BATCH_RELEASETYPE.text()
                        ]
					}
            }
    }

    message.setProperty ("differenceCount", diffSetCount)
    message.setProperty ("differenceSet", JsonOutput.toJson(diffSet))
    message.setProperty ("crudOperation", crudOperation)

    return message

}