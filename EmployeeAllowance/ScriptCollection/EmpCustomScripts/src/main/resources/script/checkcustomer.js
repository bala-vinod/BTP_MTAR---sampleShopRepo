/* Refer the link below to learn more about the use cases of script.
https://help.sap.com/viewer/368c481cd6954bdfa5d0435479fd4eaf/Cloud/en-US/148851bf8192412cba1f9d2c17f4bd25.html

If you want to know more about the SCRIPT APIs, refer the link below
https://help.sap.com/doc/a56f52e1a58e4e2bac7f7adbf45b2e26/Cloud/en-US/index.html */
importClass(com.sap.gateway.ip.core.customdev.util.Message);

function processData(message) {

    /*To set or modify the body, you can use the following methods.
    var body = message.getBody();
    message.setBody(body + " modified from js");

    //To set or modify the headers, you can use the following methods.
    var headers = message.getHeaders();
    var value = headers.get("oldHeader");
    message.setHeader("oldHeader", value + " modified");
    message.setHeader("newHeader", "newHeader");

    //To set or modify the properties, you can use the following methods.
    var properties = message.getProperties();
    value = properties.get("oldProperty");
    message.setProperty("oldProperty", value + " modified");
    message.setProperty("newProperty", "newProperty"); */

    return message;
}