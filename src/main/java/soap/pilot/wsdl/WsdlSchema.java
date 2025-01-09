package soap.pilot.wsdl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static soap.pilot.wsdl.Namespace.XSD;

public class WsdlSchema {
    @XmlElement(name = "element", namespace = XSD)
    public List<Element> elements = new ArrayList<>();

    public void addSchema(Element e){
        elements.add(e);
    }
    public static class Element {
        @XmlAttribute(name = "name")
        public String name;
        @XmlAttribute(name = "type")
        public String type;
        @XmlAttribute(name = "minOccurs")
        public String minOccurs = 0 + "";
        @XmlAttribute(name = "maxOccurs")
        public String maxOccurs = 1 + "";
        @XmlElement(namespace = XSD, name = "complexType")
        public ComplexType complexType;
        @XmlTransient
        public boolean isArray;

        public static Element element(String name, String type, int maxOccurs, int minOccurs) {
            Element element = new Element();
            element.name = name;
            element.type = type;
            element.maxOccurs = maxOccurs + "";
            element.minOccurs = minOccurs + "";
            return element;
        }

        public static Element complexType(String name, Element... elements) {
            Element element = new Element();
            element.name = name;
            element.complexType = new ComplexType();
            element.complexType.sequence.elements.addAll(Arrays.asList(elements));
            return element;
        }
    }

    public static class ComplexType {
        @XmlElement(namespace = XSD)
        public Sequence sequence = new Sequence();

        public void addElement(Element e) {
            sequence.addElement(e);
        }

    }

    public static class Sequence {
        @XmlElement(namespace = XSD, name = "element")
        public List<Element> elements = new ArrayList<>();

        public void addElement(Element e) {
            elements.add(e);
        }
    }
}
