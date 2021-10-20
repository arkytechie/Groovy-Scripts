import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

def Message processData(Message message) {
    def body = message.getBody(String.class)
    def parsedJson = new JsonSlurper().parseText(body)
    int valueSize = parsedJson.value.size()
    
StringBuilder batchBuild = new StringBuilder()
StringBuilder plantBuild = new StringBuilder()
StringBuilder matnrBuild = new StringBuilder()
 
for (int i = 0; i < valueSize; i++)
    {
        batchBuild.append("\'"+parsedJson.get("value").get(i).get("batchNumber")+"\'").toString()
        plantBuild.append("\'"+parsedJson.get("value").get(i).get("plant")+"\'").toString()
        matnrBuild.append("\'"+parsedJson.get("value").get(i).get("materialNumber")+"\'").toString()
        if (i == (valueSize-1))
            break
        else
            {
                batchBuild.append(",")
                plantBuild.append(",")
                matnrBuild.append(",")
            }
    }
//formatJson = '{"batchNumbers":'+ '\"' +batchBuild + '\"' + '}' + ',' + '{"plant":'+ '\"' +plantBuild + '\"' + '}' + ',' + '{"materialNumbers":'+ '\"' +matnrBuild + '\"' + '}'
//message.setBody(formatJson)
    message.setProperty ("batchNumbers", batchBuild)
    message.setProperty ("Plants", plantBuild)
    message.setProperty ("MaterialNumbers", matnrBuild)
    return message
}