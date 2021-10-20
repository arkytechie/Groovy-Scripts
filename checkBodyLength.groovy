import com.sap.gateway.ip.core.customdev.util.Message

def Message processData(Message message) {
    def body = message.getBody(String)
    message.setProperty('EmptyPayload', body == null || body.isEmpty() || body == '{​​​​\" value \":[]}​​​​')
    return message
}