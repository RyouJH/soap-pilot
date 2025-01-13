package soap.pilot;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soap.pilot.parser.OpenApiParser;
import soap.pilot.parser.WSDLBuilder;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class SwaggerConverterTest {
    private static final Logger log = LoggerFactory.getLogger(SwaggerConverterTest.class);
    private File resourceDir = new File("src/test/resources");

    @Test
    public void test() throws Exception {
        String yaml = TestUtils.readFile(new File(resourceDir, "swagger.yaml"), StandardCharsets.UTF_8);
        OpenAPI openAPI = new OpenAPIV3Parser().readContents(yaml).getOpenAPI();
        WSDLBuilder wsdlBuilder = new WSDLBuilder();
        OpenApiParser.parse(openAPI, wsdlBuilder);
        String expected = TestUtils.readFile(new File(resourceDir, "wsdl_expect.wsdl"), StandardCharsets.UTF_8);
        try (StringWriter writer = new StringWriter()) {
            wsdlBuilder.write(writer);
            Assert.assertEquals(expected, writer.toString());
            System.out.println(writer);
        }
    }
}