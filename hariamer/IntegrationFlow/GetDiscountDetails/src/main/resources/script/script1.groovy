import com.sap.gateway.ip.core.customdev.util.Message

/*
 * Demo error generator for GetDiscountDetails (timer driven)
 * Every 5-minute slot produces a DIFFERENT real error, in this cycle:
 *   0 -> SUCCESS                  (message goes to End)
 *   1 -> Business error           (discount condition not maintained)
 *   2 -> Connection error         (backend host unknown - UnknownHostException)
 *   3 -> Backend HTTP 500         (real call returns 500 Internal Server Error)
 *   4 -> Timeout                  (backend too slow - SocketTimeoutException)
 *   5 -> Security error           (backend returns 401 Unauthorized)
 *   6 -> Data/format error        (price not numeric - NumberFormatException)
 * Full cycle = 35 minutes.
 */
def Message processData(Message message) {
    long slot = new Date().getTime().intdiv(300000)   // 5-minute slot number
    int scenario = (int) (slot % 7)

    String goodCsv = "name,type,price\napple,fruit,30"
    message.setBody(goodCsv)

    String[] names = ["SUCCESS", "BUSINESS_ERROR", "CONNECTION_ERROR", "HTTP_500", "TIMEOUT", "SECURITY_ERROR", "DATA_ERROR"]
    message.setProperty("RunType", names[scenario])

    switch (scenario) {
        case 0:
            return message

        case 1:
            throw new Exception("Discount determination failed: pricing condition ZDIS not maintained for Customer C1001 / Material MAT-4500 / Sales Org 1710")

        case 2:
            // real DNS failure - host does not exist
            def conn = new URL("https://s4h-discount-api.releaseowl-erp.invalid/sap/opu/odata/sap/ZDISCOUNT_SRV/Discounts").openConnection()
            conn.setConnectTimeout(5000)
            conn.getResponseCode()
            return message

        case 3:
            // real backend call that returns HTTP 500
            def conn = (HttpURLConnection) new URL("https://httpbin.org/status/500").openConnection()
            conn.setConnectTimeout(5000)
            conn.setReadTimeout(10000)
            int code = conn.getResponseCode()
            if (code >= 400) {
                throw new Exception("Discount backend call failed: HTTP " + code + " " + conn.getResponseMessage() + " from ZDISCOUNT_SRV (/sap/opu/odata/sap/ZDISCOUNT_SRV/Discounts)")
            }
            return message

        case 4:
            // real read timeout - backend answers after 10s, we wait only 3s
            def conn = new URL("https://httpbin.org/delay/10").openConnection()
            conn.setConnectTimeout(5000)
            conn.setReadTimeout(3000)
            conn.getResponseCode()
            return message

        case 5:
            // real backend call rejected with 401 (technical user not authorized)
            def authConn = (HttpURLConnection) new URL("https://httpbin.org/status/401").openConnection()
            authConn.setConnectTimeout(5000)
            authConn.setReadTimeout(10000)
            int authCode = authConn.getResponseCode()
            if (authCode == 401 || authCode == 403) {
                throw new Exception("Authorization failed: HTTP " + authCode + " " + authConn.getResponseMessage() + " - technical user S4H_DISCOUNT_USER is not authorized for ZDISCOUNT_SRV")
            }
            return message

        case 6:
            // real data conversion failure - price in wrong format
            message.setBody("name,type,price\napple,fruit,30,00 INR")
            String price = "30,00 INR"
            Double.parseDouble(price)
            return message
    }
    return message
}
