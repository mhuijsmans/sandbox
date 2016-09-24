package org.mahu.unicode;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.mahu.proto.xsdtest.schema.Rule;
import org.mahu.proto.xsdtest.schema.Rules;
import org.mahu.proto.xsdtest.schema.SelectionRules;

public class AppTest {

    private static final String US_ASCII = "US-ASCII";
    private static final String UTF8 = "UTF-8";
    private static final String DEFAULT = "Default";
    private static final String PREFIX = "\u00A5\u00A5";
    private static final String ONE = "Default";

    // Unicode is a character set
    // UTF-8 is an character encoding schema; it can encode UNICODE

    // UTF-8 is a character encoding capable of encoding all possible
    // characters, or code points, defined by Unicode
    // Source: https://en.wikipedia.org/wiki/UTF-8

    @Test
    public void testApp() throws JAXBException {

        // Print unicode character; all print ¥
        System.out.println("\u00A5\u00A5");
        System.out.println((char) (Integer.parseInt("00A5", 16)));
        System.out.println((char) 0x00A5);

        SelectionRules sr1 = new SelectionRules();
        sr1.setDefault(DEFAULT);

        Rule rule1 = new Rule();
        rule1.setPrefix(PREFIX);
        rule1.setName(ONE);
        Rules rules = new Rules();
        rules.getRule().add(rule1);

        sr1.setRules(rules);

        JAXBContext context = JAXBContext.newInstance(SelectionRules.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        String xmlDoc = asString(context, sr1, US_ASCII);
        System.out.println(xmlDoc); // results in <Prefix>&#165;&#165;</Prefix>

        xmlDoc = asString(context, sr1, "ISO-8859-1");
        System.out.println(xmlDoc); // results in <Prefix>¥¥</Prefix>

        xmlDoc = asString(context, sr1, "UTF-16");
        System.out.println(xmlDoc); // results in <Prefix>¥¥</Prefix>

        xmlDoc = asString(context, sr1, UTF8);
        System.out.println(xmlDoc); // results in <Prefix>¥¥</Prefix>

        SelectionRules sr2 = toSelectionRules(context, xmlDoc);
        final String unmarshalledPrefix = sr2.getRules().getRule().get(0).getPrefix();
        System.out.println("Unmarshalled prefix: " + unmarshalledPrefix);

        assertEquals(PREFIX, unmarshalledPrefix);
    }

    public String asString(JAXBContext pContext, Object pObject, final String encoding) throws JAXBException {

        java.io.StringWriter sw = new StringWriter();

        Marshaller marshaller = pContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
        marshaller.marshal(pObject, sw);

        return sw.toString();
    }

    public SelectionRules toSelectionRules(final JAXBContext jaxbContext, final String xmlDoc) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        StringReader reader = new StringReader(xmlDoc);
        SelectionRules selectionRules = (SelectionRules) unmarshaller.unmarshal(reader);
        return selectionRules;
    }
}
