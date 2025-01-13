package soap.pilot;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soap.pilot.parser.OpenApiParser;
import soap.pilot.parser.WSDLBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
        try (StringWriter writer = new StringWriter()) {
            wsdlBuilder.write(writer);
            Assert.assertEquals(readFromFile(new File(resourceDir,"wsdl_expect.wsdl")),writer.toString());
            System.out.println(writer);
        }

    }

    private static String readFromFile(File f) throws Exception {
        try (FileInputStream fis = new FileInputStream(f);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[2048];
            int nRead = -1;
            while ((nRead = fis.read(buf)) != -1) {
                out.write(buf, 0, nRead);
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}