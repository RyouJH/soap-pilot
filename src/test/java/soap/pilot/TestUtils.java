package soap.pilot;

import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TestUtils {
    public static String readFile(String path, Charset cs) throws IOException {
        return readFile(new File(path), cs);
    }

    public static String readFile(File f, Charset cs) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(f))) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[2024];
            int nRead = -1;
            while ((nRead = in.read(buf)) != -1) {
                out.write(buf, 0, nRead);
            }
            return new String(out.toByteArray(), cs);
        }
    }

    public static byte[] readFromInputStream(InputStream source) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(source)) {
            byte[] buf = new byte[2048];
            int nRead = -1;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((nRead = in.read(buf)) != -1) {
                out.write(buf, 0, nRead);
            }
            return out.toByteArray();
        }
    }

    public static String beautifyXml(String xml) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SAXBuilder builder = new SAXBuilder();
        org.jdom2.Document resp = builder.build(new StringReader(xml));
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        outputter.output(resp, out);
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}
