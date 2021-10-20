import groovy.json.JsonSlurper

StringBuffer batchBuffer = new StringBuffer()
StringBuffer plantBuffer = new StringBuffer()
StringBuffer matnrBuffer = new StringBuffer()

def inputJson = '''{
	"value": [{
			"batchNumber": "GH22068",
			"plant": "2033",
			"materialNumber": "5905301"
		},
		{
			"batchNumber": "GH22069",
			"plant": "2038",
			"materialNumber": "5905302"
		}
	]
}'''
def json = new JsonSlurper().parseText(inputJson)
int valueSize = json.value.size()
 
for (int i = 0; i < valueSize; i++)
    {
        batchBuffer.append("\'"+json.get("value").get(i).get("batchNumber")+"\'")
        plantBuffer.append("\'"+json.get("value").get(i).get("batchNumber")+"\'")
        matnrBuffer.append("\'"+json.get("value").get(i).get("batchNumber")+"\'")
        
        if (i == (valueSize-1))
            break
        else
            {
                batchBuffer.append(",")
                plantBuffer.append(",")
                matnrBuffer.append(",")
            }
        //plantList.add("\'"+json.get("value").get(i).get("plant")+"\'")
        //matnrList.add("\'"+json.get("value").get(i).get("materialNumber")+"\'")
    }
println('Batch Number: '+ batchBuffer)
println('Plant Number: '+ plantBuffer)
println('Material Number: '+ matnrBuffer)