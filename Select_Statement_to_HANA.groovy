import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def Message processData(Message message) 
{
    def mRequestBatches = new JsonSlurper().parseText(message.getBody(String.class))
    def mapHeader = message.getHeaders()

    //def mapProperty = message.getProperties()
    def currentDbVersion = mapHeader.get("currentDbVersion")
    def incomingColumnNames = mapHeader.get("incomingColumnNames")
    def inputColumnNames = (incomingColumnNames.startsWith("(") && incomingColumnNames.endsWith(")"))?incomingColumnNames.substring(1, incomingColumnNames.length() - 1):incomingColumnNames
    //println(incomingColumnNames)
    def tableName = mapHeader.get("hanaTableName")
    //def whereClause = mapProperty.get("whereClauseProperty")
    //def batchFieldName = mapProperty.get("batchFieldNameProperty")
    //def batchVersionId = mapProperty.get("versionIdColumnName")
    //println (batchFieldName)

    def rootStart = "<NN_TEST_HANADB>\n"
    def rootEnd = "</NN_TEST_HANADB>"
    def frontendXmlString = "<Root>\n<Statement>\n<Batches action=\"SQL_QUERY\">\n<access>\n"
    def tailendXmlString = "\n</access>\n</Batches>\n</Statement>\n</Root>"
    def aRequestBatches = mRequestBatches.value

    StringBuilder outputMessage = new StringBuilder()
    outputMessage.append(rootStart)

    //SELECT col1, col2 FROM t ORDER BY 2;

                def query = "SELECT " + inputColumnNames +" FROM " + tableName
                aRequestBatches.each { requestBatch -> 
				    					    StringBuilder xmlQueryBuilder = new StringBuilder()
										    xmlQueryBuilder.append(frontendXmlString)
											xmlQueryBuilder.append(query
											//+queryValues.substring(0, queryValues.length() - 1) 
											+ " "
											//+ whereClause 
                                            + "WHERE BATCH_BATCHNUMBER = '"
											//+ requestBatch.get(batchFieldName) + "\' " 
                                            + requestBatch.batch_batchNumber + "\' " 
											+ "AND BATCH_VERSION_ID" + " = '" + currentDbVersion + "\' "
											+ tailendXmlString)
                                            outputMessage.append(xmlQueryBuilder+"\n")
                                    }

        message.setBody(outputMessage.append(rootEnd).toString())  
        return message
}
