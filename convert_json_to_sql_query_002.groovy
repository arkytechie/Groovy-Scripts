import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def Message processData(Message message) 
{
    def mRequestBatches = new JsonSlurper().parseText(message.getBody(String.class))
    def mapHeader = message.getHeaders()
    def entityInScope = mapHeader.get("entityInScope")
    def operationMethod = mapHeader.get("operationMethod")
    def scope = mapHeader.get("scope")

    def aRequestBatches = mRequestBatches.value

	def initQuery = "UPSERT "
	def tableName = "COM_NOVONORDISK_BATCHASSIGNMENT_LIMSSTATUSES "
	def columnNames = "(BATCH_BATCHNUMBER, BATCH_PLANT, BATCH_MATERIALNUMBER, BATCH_RELEASETYPE, LIMSBATCHSTATUS, LIMSURI, LIMSDISPOSITIONCODE, LIMSBATCHID)"
	//def whereClause = " WHERE BATCH_NUMBER = " 

	StringBuilder outputMessage = new StringBuilder()
	//outputMessage.append(rootStart)
	outputMessage.append(initQuery + tableName + columnNames + " VALUES ")
	//println (outputMessage)

    if ((entityInScope == "LimsStatuses") 
        && ((operationMethod == "PUT" || operationMethod == "POST" || operationMethod == "PATCH")) 
            && (scope == "single"))
            {

               aRequestBatches.each { requestBatch -> 
				   					StringBuilder xmlQueryBuilder = new StringBuilder()
									//xmlQueryBuilder.append(frontendXmlString)
									//def query = "UPSERT INTO " + tableName + " " + columnNames + " " + "VALUES ("
									StringBuilder queryValues = new StringBuilder()
                                    queryValues.append("(")		
										requestBatch.each {
											//println(it.value)
											if (it.value) {
												queryValues.append("\'"+ it.value + "\',")
												//println(queryValues)
												}
											else {
												queryValues.append("\'"+ "null" + "\',")
												//println(queryValues)
												}
										}
										/*
										xmlQueryBuilder.append(
                                                //query+
											queryValues.substring(0, queryValues.length() - 1) 
                                            + " ) WHERE BATCH_NUMBER = '"+requestBatch.batch_batchNumber+"\' "
											//+tailendXmlString
                                            )
											*/
                                            //println(queryValues)
										xmlQueryBuilder.append(queryValues.substring(0, queryValues.length() - 1) + "),\n")
                                        //xmlQueryBuilder.append("),")	
                                        //println (xmlQueryBuilder)
                                        outputMessage.append(xmlQueryBuilder.substring(0, xmlQueryBuilder.length() - 1)) 
                                        //println(outputMessage) 
                                    }
            }
            StringBuilder finalOutput = new StringBuilder ()
            finalOutput.append(outputMessage.substring(0, outputMessage.length() - 1))
            //message.setBody(outputMessage.append(rootEnd)) 
            //println(finalOutput)
            message.setBody(finalOutput.toString()) 			
            return message
}