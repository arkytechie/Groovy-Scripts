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
    def mapProperty = message.getProperties()
    def currentVersion = mapProperty.get("currentVersion")

    def rootStart = "<NN_TEST_HANADB>\n"
    def rootEnd = "</NN_TEST_HANADB>"
    def frontendXmlString = "<Root>\n<Statement>\n<Batches action=\"SQL_QUERY\">\n<access>\n"
    def tailendXmlString = "\n</access>\n</Batches>\n</Statement>\n</Root>"
    def aRequestBatches = mRequestBatches.value

    def tableName = "COM_NOVONORDISK_BATCHASSIGNMENT_"
    def tableNameSuffix = ""
    def commonColumnNames = "(BATCH_BATCHNUMBER, BATCH_PLANT, BATCH_MATERIALNUMBER, BATCH_RELEASETYPE, LIMSBATCHSTATUS, LIMSURI, LIMSDISPOSITIONCODE, LIMSBATCHID)"
    //def whereClause = " WHERE BATCH_NUMBER = " 
    
   if (entityInScope == "Components") {
        tableNameSuffix = "BATCHCOMPONENTS"
    } else {
        tableNameSuffix = entityInScope.toUpperCase()
    }
    
StringBuilder outputMessage = new StringBuilder()
outputMessage.append(rootStart)

    if ((operationMethod == "PUT" || operationMethod == "PATCH")) 
            {
                aRequestBatches.each { requestBatch -> 
				    					StringBuilder xmlQueryBuilder = new StringBuilder()
										xmlQueryBuilder.append(frontendXmlString)
										def query = "UPSERT " + tableName + tableNameSuffix + commonColumnNames + " " + "VALUES ("
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
											+ tailendXmlString)
											
                                            outputMessage.append(xmlQueryBuilder+"\n")
                                    }
            }
        //CHANGE QUERY TO PERFORM A POST OPERATION. MODIFY THE Table Name, Column Names and WHERE Clause accordingly.    
        if (operationMethod == "POST") 
            {
                aRequestBatches.each { requestBatch -> 
				    					StringBuilder xmlQueryBuilder = new StringBuilder()
										xmlQueryBuilder.append(frontendXmlString)
										def query = "INSERT INTO " + tableName + tableNameSuffix + commonColumnNames + " " + "VALUES ("
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
											+queryValues.substring(0, queryValues.length() - 1) + " ) WHERE BATCH_BATCHNUMBER = '"+requestBatch.batch_batchNumber+"\' "
											+tailendXmlString)
                                            outputMessage.append(xmlQueryBuilder+"\n")
                                    }
            }
        /*    
        //CHANGE QUERY TO PERFORM A GET OPERATION. MODIFY THE Table Name, Column Names and WHERE Clause accordingly.    
        if (operationMethod == "GET") {
                StringBuilder xmlQueryBuilder = new StringBuilder()
				xmlQueryBuilder.append(frontendXmlString)
				def query = "SELECT ID FROM VERSIONS WHERE IsActive = 'true' "
				xmlQueryBuilder.append(query+tailendXmlString) 
                outputMessage.append(xmlQueryBuilder+"\n")
            } */
        message.setBody(outputMessage.append(rootEnd))  
        return message
}
