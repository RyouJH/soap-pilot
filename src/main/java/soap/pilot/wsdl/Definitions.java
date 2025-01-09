package soap.pilot.wsdl;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(namespace = Namespace.WSDL, name = "definitions")
@XmlAccessorType(XmlAccessType.FIELD)
public class Definitions {
    @XmlAttribute(name = "targetNamespace")
    public String targetNamespace = "http://www.webserviceX.NET";
    @XmlElement(namespace = Namespace.XSD)
    public WsdlSchema schema = new WsdlSchema();
    @XmlElement(namespace = Namespace.WSDL, name = "message")
    public List<WsdlMessage> message = new ArrayList<>();
    @XmlElement(namespace = Namespace.WSDL, name = "portType")
    public List<PortType> portTypes = new ArrayList<>();
    @XmlElement(namespace = Namespace.WSDL, name = "binding")
    public List<Binding> bindings = new ArrayList<>();
    @XmlElement(namespace = Namespace.WSDL, name = "service")
    public List<Service> services = new ArrayList<>();
}

