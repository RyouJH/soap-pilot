package soap.pilot;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

public class SoapXmlBuilder {
    public SoapXmlBuilder() {
        this("http://schemas.xmlsoap.org/soap/envelope/");
    }

    public SoapXmlBuilder(String soapNamespace) {
        SOAP_NS = Namespace.getNamespace("soap", soapNamespace);
        rootElement = new Element("Envelope", SOAP_NS);
        rootElement.addNamespaceDeclaration(SOAP_NS);
        header = new Element("Header", SOAP_NS);
        rootElement.addContent(header);
    }

    private final Namespace SOAP_NS;
    private final Element rootElement;
    private final Element header;
    protected Map<String, Namespace> namespaceMap = new LinkedHashMap<>();
    protected String xml;

    public SoapXmlBuilder addNamespaceDeclare(String uri, String prefix) {
        Namespace namespace = Namespace.getNamespace(prefix, uri);
        namespaceMap.put(uri, namespace);
        rootElement.addNamespaceDeclaration(namespace);
        return this;
    }

    public SoapXmlBuilder addHeader(String name, String value) {
        return addHeader(name, value, "");
    }

    public SoapXmlBuilder addHeader(String name, String value, String namespaceUri) {
        Element h = new Element(name, namespaceMap.getOrDefault(namespaceUri, Namespace.getNamespace(namespaceUri)));
        h.setText(value);
        header.addContent(h);
        return this;
    }

    public SoapXmlBuilder addHeader(XmlElement e) {
        Element element = new Element(e.name, namespaceMap.getOrDefault(e.namespaceUrl, Namespace.getNamespace(e.namespaceUrl)));
        XmlElement.generateXml(e, element, namespaceMap);
        header.addContent(element);
        return this;
    }

    public SoapXmlBuilder setContent(String xml) throws Exception {
        this.xml = xml;
        SAXBuilder saxBuilder = new SAXBuilder();
        Document content = saxBuilder.build(new InputSource(new StringReader(xml)));
        Element bodyEl = new Element("Body", SOAP_NS);
        Element contentRootEl = content.getRootElement();
        Element contentRootElClone = new Element(contentRootEl.getName(), contentRootEl.getNamespace());
        List<Content> contents = contentRootEl.cloneContent();
        for (Content c : contents)
            contentRootElClone.addContent(c);
        bodyEl.addContent(contentRootElClone);
        rootElement.addContent(bodyEl);
        return this;
    }

    public String build(boolean pretty) throws Exception {
        Document doc = new Document();
        doc.setRootElement(rootElement);
        XMLOutputter outputter = new XMLOutputter(pretty ? Format.getPrettyFormat() : Format.getCompactFormat());
        StringWriter writer = new StringWriter();
        outputter.output(doc, writer);
        return writer.toString();
    }

    public String build() throws Exception {
        return build(false);
    }

    public static class XmlElement {
        private String name;
        private String namespaceUrl;
        private String value;
        private List<XmlElement> children = new ArrayList<>();

        public static XmlElement of(String name, String namespaceUrl, XmlElement... children) {
            XmlElement e = new XmlElement();
            e.name = name;
            e.namespaceUrl = namespaceUrl;
            e.children = Arrays.asList(children);
            return e;
        }

        public static XmlElement of(String name, String namespaceUrl, String value) {
            XmlElement e = new XmlElement();
            e.name = name;
            e.namespaceUrl = namespaceUrl;
            e.value = value;
            return e;
        }

        private static void generateXml(XmlElement e, Element parent, Map<String, Namespace> nsMap) {
            Element el = new Element(e.name, nsMap.getOrDefault(e.namespaceUrl, Namespace.getNamespace(e.namespaceUrl)));
            if (e.children.size() > 0)
                for (XmlElement child : e.children)
                    generateXml(child, el, nsMap);
            else
                el.setText(e.value);
            parent.addContent(el);
        }
    }
}
