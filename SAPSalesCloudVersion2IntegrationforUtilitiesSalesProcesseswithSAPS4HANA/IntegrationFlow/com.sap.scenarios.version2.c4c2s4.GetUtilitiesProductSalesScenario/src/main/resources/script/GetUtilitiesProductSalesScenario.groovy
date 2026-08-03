import com.sap.gateway.ip.core.customdev.util.Message;
import groovy.json.JsonSlurper;
def Message processData(Message message) {
    //constants
    final EMPTY = "";
    final ESC_SPACE = '%20';
    final ESC_SINGLE_QUOTES = '%27';
    //Get message and parse to json
    def json = message.getBody(java.io.Reader);
    def data  = new JsonSlurper().parse(json);
    //get fields of the payload (service, ressource & id)
    
    def keyDate = data?.parameters?.keyDate ?:null;
    if (keyDate == null){
      Calendar cal = Calendar.getInstance();
      def toDate = cal.time;
      def dateFormat    = 'yyyy-MM-dd';
      keyDate =  toDate.format(dateFormat);
    }
    message.setProperty('keyDate', keyDate);
    
    def deep = data?.parameters?.deep ;    
    if (deep == null){
      deep = true ;    
    }
    message.setProperty('deep', deep);

    def filterScenario = EMPTY;
    def utilitiesProductSalesScenario = data?.parameters?.utilitiesProductSalesScenario ?:"";
    if (utilitiesProductSalesScenario){
        message.setProperty('utilitiesProductSalesScenario', data?.parameters?.utilitiesProductSalesScenario ?:"");
        filterScenario = filterScenario +
        determineConnector(filterScenario) +
        'UtilitiesProductSalesScenario' + 
        ESC_SPACE + 'eq' + ESC_SPACE + 
        ESC_SINGLE_QUOTES + utilitiesProductSalesScenario + ESC_SINGLE_QUOTES;        
    }    
    def isActive = data?.parameters?.isActive;
    if (isActive != null){
        message.setProperty('isActive', isActive);
        filterScenario = filterScenario +
        determineConnector(filterScenario) +
        'IsActive' + 
        ESC_SPACE + 'eq' + ESC_SPACE + 
        isActive;
    }
    def category = data?.parameters?.category;
    if (category != null){
      message.setProperty('category', category) ;
      filterScenario = filterScenario +
        determineConnector(filterScenario) +
        'UtilsProdSlsScenarioCategory' +
        ESC_SPACE + 'eq' + ESC_SPACE +
        ESC_SINGLE_QUOTES + category + ESC_SINGLE_QUOTES;
    }
    
    if (filterScenario){
        filterScenario = '&$filter=' + filterScenario;
    }
    def language = data?.parameters?.language;
    def adjustedQuery = filterScenario;
    if (deep != false){
        
        def filterOfLanguage = "";
        if (language){
            filterOfLanguage = 'Language' + 
            ESC_SPACE + 
            'eq' + 
            ESC_SPACE + 
            ESC_SINGLE_QUOTES + 
            language + 
            ESC_SINGLE_QUOTES;
        }
        def filterOfKeyDate = 'ValidityEndDate' +
            ESC_SPACE +
            'ge' +
            ESC_SPACE +
            keyDate +
            ESC_SPACE +
            'and' +
            ESC_SPACE +
            'ValidityStartDate' +
            ESC_SPACE + 
            'le' +
            ESC_SPACE +
            keyDate;


        def filterScenarioText = "";
        if (filterOfLanguage){
            filterScenarioText = '($filter=' + filterOfLanguage + ')';
        }
        def expandScenarioText = '_ScenarioText' + filterScenarioText;

        adjustedQuery = adjustedQuery +
        '&$expand=' +  expandScenarioText + ',' +
        '_ScenarioCharacteristic($filter=' +
        filterOfKeyDate + 
        ';$expand=_CharacteristicDesc($filter=' +
        filterOfKeyDate +
        determineConnector(filterOfLanguage) +       
        filterOfLanguage +       
        '),_CharacteristicValue($filter=' +
        filterOfKeyDate +
        ';$expand=_CharcValueDesc($filter=' +
        filterOfKeyDate + 
        determineConnector(filterOfLanguage) +
        filterOfLanguage +
        ')))' 
        ;
    }
    message.setProperty('adjustedQuery', adjustedQuery);
    
    return message;

}

String determineConnector(String string ){
    final ESC_SPACE = '%20';
    // final ESC_SINGLE_QUOTES = '%27';
    if (string){
        return ESC_SPACE + 'and' + ESC_SPACE;
    } else {
        return "";
    }
}