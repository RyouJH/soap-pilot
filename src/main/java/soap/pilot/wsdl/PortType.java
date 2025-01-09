package soap.pilot.wsdl;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class PortType {
    @XmlAttribute(name = "name")
    public String name;
    @XmlElement(name = "operation", namespace = Namespace.WSDL)
    public List<Operation> operations = new ArrayList<>();

    public static class Operation {
        @XmlAttribute
        public String name;
        @XmlElement(namespace = Namespace.WSDL)
        public Input input;
        @XmlElement(namespace = Namespace.WSDL)
        public Output output;

        public static Operation of(String input, String output) {
            Operation op = new Operation();
            op.input = Input.of(input);
            op.output = Output.of(output);
            return op;
        }

        public static class Input {
            @XmlAttribute
            public String message;

            public static Input of(String message) {
                Input input = new Input();
                input.message = message;
                return input;
            }
        }

        public static class Output {
            @XmlAttribute
            public String message;

            public static Output of(String message) {
                Output output = new Output();
                output.message = message;
                return output;
            }
        }
    }
}
