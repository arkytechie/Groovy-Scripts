import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def Message processData(Message message) 
{
    def mRequestBatches = new JsonSlurper().parseText(message.getBody(String.class))
    def mapHeader = message.getHeaders()
    def entityInScope = mapHeader.get("entityInScope")
    def mapProperty = message.getProperties()

    def tableName = "COM_NOVONORDISK_BATCHASSIGNMENT_"
    def tableNameSuffix = ""
    StringBuilder columnNames = new StringBuilder()
    def whereClause = " WHERE " 
    
   if (entityInScope == "Components") {
        tableNameSuffix = "BATCHCOMPONENTS"
    } else {
        tableNameSuffix = entityInScope.toUpperCase()
    }
    //Table Name Finalized
    message.setProperty ("tableNameProperty", tableName+tableNameSuffix)
	
	//Complete LIST of column names
	StringBuilder xmlFieldBuilder = new StringBuilder()
	xmlFieldBuilder.append("(")
	StringBuilder keyFieldValues = new StringBuilder()

	def aRequestBatches = mRequestBatches.value
	aRequestBatches.any { requestBatch -> 		
							requestBatch.each {
											if (it.key) {
												keyFieldValues.append(it.key.toUpperCase() + ",")
											}
							}
							xmlFieldBuilder.append( keyFieldValues.substring(0, keyFieldValues.length() - 1)
							+ ")"
							)
							columnNames.append(xmlFieldBuilder+"\n")
                            }
	message.setProperty ("columnNameProperty", columnNames)
    
    StringBuilder finalWhereClause = new StringBuilder()

	StringBuilder whereClauseBuilder = new StringBuilder()
	whereClauseBuilder.append(" WHERE ")
	StringBuilder batchField = new StringBuilder()

    //Complete LIST of fields with where clauses
    aRequestBatches.any { requestBatch -> 	
							requestBatch.each {
											if (it.key.toUpperCase().contains("BATCHNUMBER")) {
                                                message.setProperty("batchFieldNameProperty", it.key)
												batchField.append(it.key.toUpperCase())
												}
							}
							whereClauseBuilder.append(batchField + " = '")
                            finalWhereClause.append(whereClauseBuilder)
                            }
    message.setProperty ("whereClauseProperty", finalWhereClause)
	
	return message
}
