package soap.pilot.parser;

import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;
import soap.pilot.parser.OpenApiParser.OpenAPIParseHandler;
import soap.pilot.wsdl.*;
import soap.pilot.wsdl.Binding.BindingOperation;
import soap.pilot.wsdl.Binding.BindingOperation.BindingOpInputOutput;
import soap.pilot.wsdl.Binding.BindingOperation.SoapOperation;
import soap.pilot.wsdl.Binding.SoapBinding;
import soap.pilot.wsdl.PortType.Operation;
import soap.pilot.wsdl.PortType.Operation.Input;
import soap.pilot.wsdl.PortType.Operation.Output;
import soap.pilot.wsdl.Service.ServicePort;
import soap.pilot.wsdl.Service.ServicePort.SoapAddress;
import soap.pilot.wsdl.WsdlMessage.Part;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WSDLBuilder implements OpenAPIParseHandler {
    public final Definitions definitions = new Definitions();

    @Override
    public void onStart() {

    }

    @Override
    public void onServer(Server server) {
        Service service = new Service();
        String serviceName = Optional.ofNullable(server.getExtensions()).map(m -> m.getOrDefault("x-service-name", "SoapService")).map(Object::toString)
                .get();
        service.name = serviceName;
        service.url = server.getUrl();
        definitions.services.add(service);
    }

    @Override
    public void onStartSchema() {

    }

    @Override
    public void onSchema(String id, Schema schema) {
        OpenAPISchemaVisitor schemaVisitor = new OpenAPISchemaVisitor();
        schemaVisitor.visit(id, schema);
        definitions.schema.addSchema(schemaVisitor.element);
        Map<String, List<Map<String, String>>> exts = schema.getExtensions();
        if (exts == null || !exts.containsKey("x-message"))
            throw new IllegalArgumentException("extention field 'x-message' is required");
        List<Map<String, String>> xMessages = exts.get("x-message");
        for (Map<String, String> xMessage : xMessages) {
            Part part = Part.of("tns:" + id, xMessage.get("part"));
            definitions.message.add(WsdlMessage.of(xMessage.get("name"), part));
        }
    }

    @Override
    public void onFinishSchema() {

    }

    @Override
    public void onStartPathItem() {

    }

    @Override
    public void onPathItem(String path, PathItem item) {
        List<PortType> portTypes = definitions.portTypes;
        List<Binding> bindings = definitions.bindings;
        PortType portType = new PortType();
        Binding binding = new Binding();
        Map<String, Object> pathItemExts = Optional.ofNullable(item.getExtensions()).orElseThrow(() -> new NullPointerException("extension headers not found"));
        if (!pathItemExts.containsKey("x-soap-port-type"))
            throw new NullPointerException("'x-soap-port-type' header is requred in path item. path : " + path);
        binding.name = pathItemExts.get("x-soap-binding-name") + "";
        portType.name = pathItemExts.get("x-soap-port-type") + "";
        binding.type = "tns:" + portType.name;
        String transport = Optional.ofNullable(pathItemExts.get("x-soap-binding-transport")).map(Object::toString).orElse("http://schemas.xmlsoap.org/soap/http");
        binding.soapBinding = SoapBinding.of(transport);
        item.readOperationsMap().forEach((method, op) -> {
            Operation operation = new Operation();
            BindingOperation bindingOperation = new BindingOperation();
            RequestBody requestBody = op.getRequestBody();
            operation.name = Optional.ofNullable(op.getOperationId()).orElseThrow(() -> new NullPointerException("operation id is required."));
            Map<String, Object> opExts = op.getExtensions();
            if (!opExts.containsKey("x-soap-soapAction"))
                throw new IllegalArgumentException("op extention field 'x-soap-soapAction' is required");
            bindingOperation.name = op.getOperationId();
            bindingOperation.soapOperations = SoapOperation.of(opExts.get("x-soap-soapAction") + "");
            Optional.ofNullable(requestBody.getContent()).map(c -> c.get("application/soap+xml"))
                    .map(MediaType::getExtensions)
                    .map(xmlTypeExts -> xmlTypeExts.get("x-soap-portType-operation-input"))
                    .map(Object::toString)
                    .ifPresent(input -> {
                        operation.input = Input.of("tns:" + input);
                        bindingOperation.input = BindingOpInputOutput.of("literal");
                    });
            Optional.ofNullable(op.getResponses()).map(res -> res.get("200"))
                    .map(ApiResponse::getContent)
                    .map(c -> c.get("application/soap+xml"))
                    .map(MediaType::getExtensions).ifPresent(exts -> {
                        Object output = exts.get("x-soap-portType-operation-output");
                        operation.output = Output.of(output + "");
                        bindingOperation.output = BindingOpInputOutput.of("literal");
                    });

            binding.operation.add(bindingOperation);
            portType.operations.add(operation);
            for (Service service : definitions.services) {
                ServicePort port = new ServicePort();
                port.name = portType.name;
                port.binding = "tns:" + binding.name;
                port.soapAddress = new SoapAddress();
                port.soapAddress.location = service.url + path;
                service.ports.add(port);
            }
        });
        bindings.add(binding);
        portTypes.add(portType);

    }

    @Override
    public void onFinishPathItem() {

    }

    public void write(Writer writer) throws Exception {
        JAXBContext ctx = JAXBContext.newInstance(Definitions.class);
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(definitions, writer);
    }
}
