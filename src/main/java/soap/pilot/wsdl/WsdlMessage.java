package soap.pilot.wsdl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WsdlMessage {
    @XmlAttribute
    public String name;

    @XmlElement(name = "part",namespace = Namespace.WSDL)
    public List<Part> part = new ArrayList<>();

    public static WsdlMessage of(String name, Part... part) {
        WsdlMessage message = new WsdlMessage();
        message.name = name;
        message.part.addAll(Arrays.asList(part));
        return message;
    }

    public static class Part {
        @XmlAttribute
        public String name;
        @XmlAttribute
        public String element;

        public static Part of(String element, String name) {
            Part part = new Part();
            part.element = element;
            part.name = name;
            return part;
        }
    }
}
