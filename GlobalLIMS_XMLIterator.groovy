import groovy.util.XmlSlurper

String globalLIMS = '''
<value><batchNumber>GH22068</batchNumber><plant>2033</plant><materialNumber>5905301</materialNumber></value>
<value><batchNumber>GH22069</batchNumber><plant>2038</plant><materialNumber>5905312</materialNumber></value>
'''
StringBuilder batchBuild = new StringBuilder()
StringBuilder plantBuild = new StringBuilder()
StringBuilder matnrBuild = new StringBuilder()

def parsedXml = new XmlSlurper().parseText(globalLIMS)
def Val = parsedXml.value 
int valueSize = parsedXml.value.size()
    
for (int i = 0; i < valueSize; i++)
    {
        batchBuild.append("\'"+ Val[i].batchNumber.text() +"\'")
        plantBuild.append("\'"+ Val[i].plant.text() +"\'")
        matnrBuild.append("\'"+ Val[i].materialNumber.text() +"\'")
        //Val[i].batchNumber.text()
         if (i == (valueSize-1))
            break
        else
            {
                batchBuild.append(",")
                plantBuild.append(",")
                matnrBuild.append(",")
            }     }
println (batchBuild)
println (plantBuild)
println (matnrBuild)