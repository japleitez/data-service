package eu.europa.ec.eurostat.wihp.faker;

import eu.europa.ec.eurostat.wihp.service.dto.ParserFilterDTO;

public class ParserFilterDTOFaker {

    public static ParserFilterDTO createParserFilterDTO(String name, String classname) {
        ParserFilterDTO dto = new ParserFilterDTO();
        dto.setName(name);
        dto.setClassName(classname);
        return dto;
    }

}
