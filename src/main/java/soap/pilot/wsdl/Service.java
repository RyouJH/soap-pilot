package soap.pilot.wsdl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;

public class Service {
    @XmlTransient
    public String url;
    @XmlAttribute(name = "name")
    public String name;
    @XmlElement(name = "port", namespace = Namespace.WSDL)
    public List<ServicePort> ports = new ArrayList<>();

    public static class ServicePort {
        @XmlAttribute(name = "name")
        public String name;
        @XmlAttribute(name = "binding")
        public String binding;
        @XmlElement(name = "address", namespace = Namespace.SOAP)
        public SoapAddress soapAddress;

        public static class SoapAddress {
            @XmlAttribute(name = "location")
            public String location;

            public static SoapAddress of(String location) {
                SoapAddress sa = new SoapAddress();
                sa.location = location;
                return sa;
            }
        }
    }
}
