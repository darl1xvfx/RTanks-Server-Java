package rt.server.battles.maps.parser.parser;

import rt.server.battles.maps.parser.parser.map.Map;
import jakarta.xml.bind.*;
import java.io.*;

public class Parser
{
    private Unmarshaller unmarshaller;
    
    public Parser() {
    	try {
            final JAXBContext jc = JAXBContext.newInstance(Map.class);
            this.unmarshaller = jc.createUnmarshaller();
    	} catch (JAXBException e) {
    		e.printStackTrace();
    	}
    }
    
    public Map parseMap(final File file) throws JAXBException {
        return (Map)this.unmarshaller.unmarshal(file);
    }
}
