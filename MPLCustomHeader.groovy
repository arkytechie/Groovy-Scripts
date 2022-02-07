import com.sap.gateway.ip.core.customdev.util.Message;

def Message setCustomHeader(Message message) {
    
	def messageLog = messageLogFactory.getMessageLog(message);
	if(messageLog != null){

        //define headers
        def prop = message.getProperties();
        def participantId = prop.get("participantId");
        if (participantId!=null) {
            message.setHeader("SAP_MessageType", "person_" + participantId);
        }
        
        //define custom header
        def personId = prop.get("id");
		if (personId!=null && personId!='none' && personId!='all') {
			messageLog.addCustomHeaderProperty("personId", personId);		
        }
        def month = prop.get("month");
		if (month!=null) {
			messageLog.addCustomHeaderProperty("month", month);		
        }
	}
	return message;
}