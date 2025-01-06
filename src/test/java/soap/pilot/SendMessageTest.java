package soap.pilot;

import com.sun.net.httpserver.HttpServer;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.StringJoiner;
import java.util.concurrent.CountDownLatch;

public class SendMessageTest {
    private File resourceDir = new File("src/test/resources");
    private File sendMessageFile = new File(resourceDir, "SoapSendMessage.xml");
    private CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void test() throws Exception {
        try (SoapReceiver _ = SoapReceiver.start("localhost", 28080, latch::countDown)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder parser = factory.newDocumentBuilder();

            //request SOAP message DOMSource create
            String sendMessage =
                    new SoapXmlBuilder()
                            .addHeader("Authorization", Base64.getEncoder().encodeToString("TESTASFDASF".getBytes()))
                            .setContent(TestUtils.readFile(sendMessageFile, StandardCharsets.UTF_8)).build();
            StringReader reader = new StringReader(sendMessage);
            InputSource is = new InputSource(reader);
            Document document = parser.parse(is);
            DOMSource requestSource = new DOMSource(document);

//        //SOAPMessage create
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage requestSoapMessage = messageFactory.createMessage();
            SOAPPart requestSoapPart = requestSoapMessage.getSOAPPart();
            requestSoapPart.setContent(requestSource);

            //SOAPConnection create instance
            SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = scf.createConnection();
            SOAPMessage responseSoapMessage = connection.call(requestSoapMessage, "http://localhost:28080/soap");

            System.out.println("-----\nresponse : ");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            responseSoapMessage.writeTo(out);
            System.out.println(TestUtils.beautifyXml(new String(out.toByteArray())));

            latch.await();
        }
    }

    private static class SoapReceiver implements AutoCloseable {
        private HttpServer server;

        public interface Callback {
            void callback();
        }

        public static SoapReceiver start(String host, int port, SoapReceiver.Callback onReceive) throws Exception {
            SoapReceiver receiver = new SoapReceiver();
            receiver.server = HttpServer.create(new InetSocketAddress(host, port), 0);
            receiver.server.createContext("/soap", httpExchange -> {
                try {
                    byte[] request = TestUtils.readFromInputStream(httpExchange.getRequestBody());
                    String xml = new String(request, StandardCharsets.UTF_8);
                    System.out.printf("--------------------\nSOAP REQUEST : \n%s\n", xml);
                    httpExchange.getResponseHeaders().add("Content-Type", "text/xml");
                    httpExchange.sendResponseHeaders(200, 0);
                    SoapXmlBuilder builder = new SoapXmlBuilder();
                    httpExchange.getRequestHeaders().forEach((k, v) -> {
                        StringJoiner joiner = new StringJoiner(",");
                        for (String i : v)
                            joiner.add(i);
                        builder.addHeader(k, joiner.toString());
                    });
                    builder.setContent(extractBody(xml));
                    try (OutputStreamWriter osw = new OutputStreamWriter(httpExchange.getResponseBody(), "UTF-8")) {
                        osw.write(builder.build());
                    }
                    onReceive.callback();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            receiver.server.start();
            return receiver;
        }

        public void close() {
            server.stop(0);
        }

        private static String extractBody(String soap) throws Exception {
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage(
                    null,
                    new ByteArrayInputStream(soap.getBytes())
            );
            SOAPBody soapBody = soapMessage.getSOAPBody();
            Document document = soapBody.extractContentAsDocument();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            transformer.transform(new DOMSource(document), new StreamResult(out));
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
