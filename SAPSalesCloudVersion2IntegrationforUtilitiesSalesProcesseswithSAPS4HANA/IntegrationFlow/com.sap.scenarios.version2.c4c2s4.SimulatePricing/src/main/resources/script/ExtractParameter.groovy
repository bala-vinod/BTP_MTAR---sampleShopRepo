import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper;

def Message processData(Message message) {
    def messageBodyJson = message.getBody(java.io.Reader);
    def messageBodyData = new JsonSlurper().parse(messageBodyJson);
    def product = messageBodyData?.parameters?.product;
    
    if (product) {
        message.setProperty("Product", product);
    }
    
    return message
}