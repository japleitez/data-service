package eu.europa.ec.eurostat.wihp.faker;


import eu.europa.ec.eurostat.wihp.service.dto.SourceDTO;

import java.util.ArrayList;
import java.util.List;

public class SourceFaker {

    public static SourceDTO createFakeSourceDTO() {
        SourceDTO dto = new SourceDTO();
        dto.setName(NameFaker.generateAlphabeticString(10));
        dto.setUrl(UrlFaker.generateUrl());
        return dto;
    }

    public static List<SourceDTO> createFakeSourceDTOs(int number) {
        List<SourceDTO> result = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            result.add(createFakeSourceDTO());
        }
        return result;
    }

}
