@XmlSchema(
        namespace = Namespace.WSDL,
        xmlns = {
                @XmlNs(namespaceURI = Namespace.WSDL, prefix = "wsdl"),
                @XmlNs(namespaceURI = "http://schemas.xmlsoap.org/wsdl/http/", prefix = "http"),
                @XmlNs(namespaceURI = "http://schemas.xmlsoap.org/wsdl/soap/", prefix = "soap"),
                @XmlNs(namespaceURI = "http://www.w3.org/2001/XMLSchema", prefix = "xs"),
                @XmlNs(namespaceURI = "http://www.webserviceX.NET",prefix = "tns")
        }
)
package soap.pilot.wsdl;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;

