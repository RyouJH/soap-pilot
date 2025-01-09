package soap.pilot.wsdl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class Binding {
    @XmlAttribute(name = "name")
    public String name;
    @XmlAttribute(name = "type")
    public String type;
    @XmlElement(name = "binding", namespace = Namespace.SOAP)
    public SoapBinding soapBinding;
    @XmlElement(name = "binding", namespace = Namespace.HTTP)
    public HTTPBinding httpBinding;

    @XmlElement(name = "operation", namespace = Namespace.WSDL)
    public List<BindingOperation> operation = new ArrayList<>();

    public static class SoapBinding {
        @XmlAttribute(name = "transport")
        public String transport;

        public static SoapBinding of(String transport) {
            SoapBinding soapBinding = new SoapBinding();
            soapBinding.transport = transport;
            return soapBinding;
        }
    }

    public static class BindingOperation {
        @XmlAttribute(name = "name")
        public String name;
        @XmlElement(name = "operation", namespace = Namespace.SOAP)
        public SoapOperation soapOperations;
        @XmlElement(name = "operation", namespace = Namespace.HTTP)
        public HttpOperation httpOperation;
        @XmlElement(name = "input", namespace = Namespace.WSDL)
        public BindingOpInputOutput input;
        @XmlElement(name = "output", namespace = Namespace.WSDL)
        public BindingOpInputOutput output;

        public static class SoapOperation {
            @XmlAttribute(name = "soapAction")
            public String soapAction;
            @XmlAttribute(name = "style")
            public String style = "document";

            public static SoapOperation of(String soapAction, String style) {
                SoapOperation soapOperation = new SoapOperation();
                soapOperation.soapAction = soapAction;
                soapOperation.style = style;
                return soapOperation;
            }

            public static SoapOperation of(String soapAction) {
                SoapOperation soapOperation = new SoapOperation();
                soapOperation.soapAction = soapAction;
                return soapOperation;
            }
        }

        public static class HttpOperation {
            @XmlAttribute(name = "location")
            public String location;

            public static HttpOperation of(String location) {
                HttpOperation op = new HttpOperation();
                op.location = location;
                return op;
            }
        }

        public static class BindingOpInputOutput {
            @XmlElement(name = "body", namespace = Namespace.SOAP)
            public SoapBody body;

            public static BindingOpInputOutput of(String use) {
                BindingOpInputOutput o = new BindingOpInputOutput();
                o.body = SoapBody.of(use);
                return o;
            }

            public static class SoapBody {
                @XmlAttribute
                public String use;

                public static SoapBody of(String use) {
                    SoapBody soapBody = new SoapBody();
                    soapBody.use = use;
                    return soapBody;
                }
            }
        }
    }

    public static class HTTPBinding {
        @XmlAttribute(name = "verb")
        public String verb;

        public static HTTPBinding of(String verb) {
            HTTPBinding hb = new HTTPBinding();
            hb.verb = verb;
            return hb;
        }

    }

}
