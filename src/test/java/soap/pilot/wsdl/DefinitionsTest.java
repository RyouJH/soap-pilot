package soap.pilot.wsdl;

import org.junit.Test;
import soap.pilot.wsdl.WsdlSchema.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class DefinitionsTest {

    @Test
    public void test() throws Exception {
        Definitions definitions = new Definitions();
        WsdlSchema schema = definitions.schema;
        schema.elements.add(Element.element("RequestId", "xsd:string", 0, 1));
        {
            Element getWeather = Element.complexType(
                    "GetWeather",
                    Element.element("CityName", "xsd:string", 0, 1),
                    Element.element("CountryName", "xsd:string", 0, 1)
            );
            schema.elements.add(getWeather);
        }
        {
            Element element = Element.complexType("GetWeatherResponse", Element.element("GetWeatherResult", "xsd:string", 0, 1));
            schema.elements.add(element);
        }
        JAXBContext ctx = JAXBContext.newInstance(Definitions.class);
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(definitions, System.out);
    }

}