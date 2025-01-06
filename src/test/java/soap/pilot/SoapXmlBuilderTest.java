package soap.pilot;

import soap.pilot.SoapXmlBuilder.XmlElement;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class SoapXmlBuilderTest {
    @Test
    public void test() throws Exception {
        String body = TestUtils.readFile(new File("src/test/resources/SoapSendMessage.xml"), StandardCharsets.UTF_8);
        String xml = new SoapXmlBuilder()
                .addNamespaceDeclare("http://inspien.co.kr", "ins")
                .addNamespaceDeclare("http://inspien.co.kr/lab/", "lab")
                .addHeader("Authentication", "Bearer token123")
                .addHeader("Authentication", "Bearer token123", "http://inspien.co.kr")
                .addHeader(
                        XmlElement.of("BasicAuth", "http://inspien.co.kr",
                                XmlElement.of("username", "http://inspien.co.kr/lab/", "rjh926"),
                                XmlElement.of("password", "http://inspien.co.kr/lab/", "p@ssw>0rd!!")
                        ))
                .setContent(body)
                .build(true);
        System.out.println(xml);
    }
}