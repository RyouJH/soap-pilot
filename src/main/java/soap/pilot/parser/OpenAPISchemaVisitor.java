package soap.pilot.parser;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.XML;
import soap.pilot.wsdl.WsdlSchema.ComplexType;
import soap.pilot.wsdl.WsdlSchema.Element;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

public class OpenAPISchemaVisitor implements SchemaVisitor {
    public Element element;
    private final Stack<Element> stack = new Stack<>();

    @Override
    public void visit(String id, Schema schema) {
        if (schema.getType() == null)
            return;
        switch (schema.getType()) {
            case "object":
                element = new Element();
                if(!stack.isEmpty() && stack.peek().isArray)
                    element.maxOccurs = getMaximum(schema);
                element.name = Optional.ofNullable(schema.getXml()).map(XML::getName).orElse(id);
                element.complexType = new ComplexType();
                Set<Entry<String, Schema>> set = schema.getProperties().entrySet();
                for (Entry<String, Schema> e : set) {
                    stack.push(element);
                    visit(e.getKey(), e.getValue());
                    Element child = element;
                    element = stack.pop();
                    element.complexType.addElement(child);
                }
                break;
            case "string":
                element = new Element();
                if(!stack.isEmpty() && stack.peek().isArray)
                    element.maxOccurs = getMaximum(schema);
                element.name = Optional.ofNullable(schema.getXml()).map(XML::getName).orElse(id);
                element.type = "xs:string";
                break;
            case "integer":
                element = new Element();
                if(!stack.isEmpty() && stack.peek().isArray)
                    element.maxOccurs = getMaximum(schema);
                element.name = Optional.ofNullable(schema.getXml()).map(XML::getName).orElse(id);
                element.type = "xs:integer";
                break;
            case "array":
                element = new Element();
                element.complexType = new ComplexType();
                element.isArray = true;
                element.name = Optional.ofNullable(schema.getXml()).map(XML::getName).orElse(id);
                Schema items = schema.getItems();
                stack.push(element);
                visit(id, items);
                Element child = element;
                element = stack.pop();
                element.complexType.addElement(child);
                break;
        }
    }

    private static String getMaximum(Schema schema) {
        return Optional.ofNullable(schema.getMaximum()).map(v -> v + "").orElse("unbounded");
    }
}
