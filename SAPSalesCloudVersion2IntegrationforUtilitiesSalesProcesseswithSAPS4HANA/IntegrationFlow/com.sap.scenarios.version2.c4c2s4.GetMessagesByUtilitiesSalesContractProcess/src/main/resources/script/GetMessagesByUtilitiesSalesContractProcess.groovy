import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper;

def Message readParameters(Message message) {
    //Get message and parse to json
    def json = message.getBody(java.io.Reader);
    def data  = new JsonSlurper().parse(json);
    //get fields of the payload (service, ressource & id)
    
    message.setProperty('utilsSalesContractProcessUuid', data?.parameters?.utilsSalesContractProcessUuid ?:"");
    return message;
}