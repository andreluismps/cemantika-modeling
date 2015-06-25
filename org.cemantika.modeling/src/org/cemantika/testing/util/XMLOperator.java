package org.cemantika.testing.util;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.cemantika.testing.model.Scenario;

public class XMLOperator {

	public static File generateXMLFile(Scenario scenario, File xmlSimulador) {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(Scenario.class);
				
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		m.marshal(scenario, xmlSimulador);
		
		XMLInputFactory xif = XMLInputFactory.newFactory();
        StreamSource xml = new StreamSource(xmlSimulador);
        XMLStreamReader xsr = xif.createXMLStreamReader(xml);
        xsr.nextTag();
        while(!xsr.getLocalName().equals("suite")) {
            xsr.nextTag();
        }
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (XMLStreamException e) {
			// TODO: handle exception
		}        
		return xmlSimulador;
	}
	
	public Scenario readXML(File file){
		return null;
	}

}
