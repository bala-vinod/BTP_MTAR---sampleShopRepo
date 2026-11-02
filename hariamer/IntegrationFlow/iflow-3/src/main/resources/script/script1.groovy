package script

import com.sap.gateway.ip.core.customdev.util.Message

/*
 * Demo error generator for iflow-3 (timer driven, Start Timer = every 5 minutes)
 * Each 5-minute slot gives a DIFFERENT real error. Cycle = 35 minutes.
 * OFFSET shifts the cycle so iflow-3 does not fail with the same error
 * at the same time as the other iFlows.
 */
def Message processData(Message message) {
    String IFLOW  = "iflow-3"
    int    OFFSET = 6

    long slot = new Date().getTime().intdiv(300000)   // 5-minute slot number
    int scenario = (int) ((slot + OFFSET) % 7)

    String[] names = ["SUCCESS", "BUSINESS_ERROR", "CONNECTION_ERROR", "HTTP_500", "TIMEOUT", "AUTH_401", "DATA_ERROR"]
    message.setProperty("RunType", names[scenario])

    switch (scenario) {
        case 0:
            return message

        case 1:
            throw new Exception(IFLOW + ": Invoice 9000007788 already posted - duplicate document number in Company Code 1710")

        case 2:
            // real DNS failure - host does not exist
            def c2 = new URL("https://s4h-billing-api.releaseowl-erp.invalid/sap/opu/odata/sap/API_BILLING_DOCUMENT_SRV/").openConnection()
            c2.setConnectTimeout(5000)
            c2.getResponseCode()
            return message

        case 3:
            // real backend call that returns HTTP 500
            def c3 = (HttpURLConnection) new URL("https://httpbin.org/status/500").openConnection()
            c3.setConnectTimeout(5000)
            c3.setReadTimeout(10000)
            int code3 = c3.getResponseCode()
            if (code3 >= 400) {
                throw new Exception(IFLOW + ": backend call failed: HTTP " + code3 + " " + c3.getResponseMessage() + " from API_BILLING_DOCUMENT_SRV")
            }
            return message

        case 4:
            // real read timeout - backend answers after 10s, we wait only 3s
            def c4 = new URL("https://httpbin.org/delay/10").openConnection()
            c4.setConnectTimeout(5000)
            c4.setReadTimeout(3000)
            c4.getResponseCode()
            return message

        case 5:
            // real backend call rejected with 401
            def c5 = (HttpURLConnection) new URL("https://httpbin.org/status/401").openConnection()
            c5.setConnectTimeout(5000)
            c5.setReadTimeout(10000)
            int code5 = c5.getResponseCode()
            if (code5 == 401 || code5 == 403) {
                throw new Exception(IFLOW + ": authorization failed: HTTP " + code5 + " " + c5.getResponseMessage() + " - technical user S4H_BILLING_USER is not authorized for API_BILLING_DOCUMENT_SRV")
            }
            return message

        case 6:
            // real data conversion failure
            Double.parseDouble("N/A")
            return message
    }
    return message
}
