import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def Message processData(Message message) 
{
    //def mRequestBatches = new JsonSlurper().parseText(message.getBody(String.class))
    //def mapHeader = message.getHeaders()

    def mapProperty = message.getProperties()
    def currentVersion = mapProperty.get("currentVersion")
	def crudOperation = mapProperty.get("crudOperation")
	def tableName = mapProperty.get("hanaTableName")
	def diffSet = mapProperty.get("differenceSet")
    def currentDbVersion = mapProperty.get("currentDbVersion")
	def mRequestBatches = new JsonSlurper().parseText(diffSet)

    def rootStart = "<NN_TEST_HANADB>\n"
    def rootEnd = "</NN_TEST_HANADB>"
    def frontendXmlString = "<Root>\n<Statement>\n<Batches action=\"SQL_QUERY\">\n<access>\n"
    def tailendXmlString = "\n</access>\n</Batches>\n</Statement>\n</Root>"
    def aRequestBatches = mRequestBatches.value
	
StringBuilder outputMessage = new StringBuilder()
outputMessage.append(rootStart)

    if (crudOperation == "DELETE") 
            {
                mRequestBatches.each { requestBatch -> 
				    					StringBuilder xmlQueryBuilder = new StringBuilder()
										xmlQueryBuilder.append(frontendXmlString)
										def query = crudOperation + " FROM " + tableName + " WHERE ("
										StringBuilder queryValues = new StringBuilder()		
											requestBatch.each {
												//println(it.value)
                                                queryValues.append(it.key.toUpperCase() + " = ")
												if (it.value) {
													queryValues.append("\'"+ it.value + "\' AND ")
													//println(queryValues)
												}
												else {
													queryValues.append("\'"+ "NULL" + "\',")
													//println(queryValues)
												}
											}
											xmlQueryBuilder.append(query
											+queryValues.substring(0, queryValues.length() - 4) 
											//+ " ) WHERE BATCH_BATCHNUMBER = '"+requestBatch.batch_batchNumber+"\' " 
											+ ") AND BATCH_VERSION_ID = " + currentDbVersion
											+ tailendXmlString)
											
                                            outputMessage.append(xmlQueryBuilder+"\n")
                                    }
            }
			
		if (crudOperation == "UPSERT") 
            {
                aRequestBatches.each { requestBatch -> 
				    					StringBuilder xmlQueryBuilder = new StringBuilder()
										xmlQueryBuilder.append(frontendXmlString)
										def query = crudOperation + tableName + commonColumnNames + " VALUES ("
										StringBuilder queryValues = new StringBuilder()		
											requestBatch.each {
												//println(it.value)
												if (it.value) {
													queryValues.append("\'"+ it.value + "\',")
													//println(queryValues)
												}
												else {
													queryValues.append("\'"+ "NULL" + "\',")
													//println(queryValues)
												}
											}
											xmlQueryBuilder.append(query
											+queryValues.substring(0, queryValues.length() - 1) 
											+ " ) WHERE BATCH_BATCHNUMBER = '"+requestBatch.batch_batchNumber+"\' " 
											+ "AND BATCH_VERSION_ID = '" + currentVersion + "\' "
                                            + "WITH PRIMARY KEY"
											+ tailendXmlString)
											
                                            outputMessage.append(xmlQueryBuilder+"\n")
                                    }
            }	
       
        message.setBody(outputMessage.append(rootEnd).toString())
    return message
}
