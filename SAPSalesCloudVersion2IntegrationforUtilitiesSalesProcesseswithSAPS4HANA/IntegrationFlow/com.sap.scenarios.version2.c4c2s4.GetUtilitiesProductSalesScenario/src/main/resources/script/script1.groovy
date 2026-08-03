import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper;
def Message processData(Message message) {
    //Get message and parse to json
    def json = message.getBody(java.io.Reader);
    def data  = new JsonSlurper().parse(json);
    //get fields of the payload (service, ressource & id)
    message.setProperty('utilitiesProductSalesScenario', data?.parameters.utilitiesProductSalesScenario ?:"");
    def keyDate = data?.parameters?.keyDate ?:null;
    if (keyDate == null){
      Calendar cal = Calendar.getInstance();
      def toDate = cal.time;
      def dateFormat    = 'yyyy-MM-dd';
      keyDate =  toDate.format(dateFormat);
    }
    message.setProperty('keyDate', keyDate);
    def deep = false;
    if (data?.parameters?.deep ?:null != null){
      deep = data?.parameters?.deep;
    }
    message.setProperty('deep', deep);
    
    def adjustedQuery = '&$expand=_ScenarioCharacteristic($filter=ValidityEndDate%20ge%20$' + keyDate;
    adjustedQuery = adjustedQuery + '%20and%20ValidityStartDate%20le%20';
    adjustedQuery = adjustedQuery + keyDate;
    adjustedQuery = adjustedQuery + ';$expand=_CharacteristicDesc($filter=ValidityEndDate%20ge%20';
    adjustedQuery = adjustedQuery + keyDate;
    adjustedQuery = adjustedQuery + '%20and%20ValidityStartDate%20le%20';
    adjustedQuery = adjustedQuery + keyDate;
    adjustedQuery = adjustedQuery + '),_CharacteristicValue($filter=ValidityEndDate%20ge%20';
    adjustedQuery = adjustedQuery + keyDate;
    adjustedQuery = adjustedQuery + '%20and%20ValidityStartDate%20le%20';
    adjustedQuery = adjustedQuery + keyDate;
    adjustedQuery = adjustedQuery + ';$expand=_CharcValueDesc($filter=ValidityEndDate%20ge%20';
    adjustedQuery = adjustedQuery + keyDate;
    adjustedQuery = adjustedQuery + '%20and%20ValidityStartDate%20le%20';
    adjustedQuery = adjustedQuery + keyDate;
    adjustedQuery = adjustedQuery + '))),_ScenarioText';
    message.setProperty('adjustedQuery', adjustedQuery);
    return message;
}