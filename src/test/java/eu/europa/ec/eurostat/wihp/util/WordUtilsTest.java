package eu.europa.ec.eurostat.wihp.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordUtilsTest {

    @Test
    public void tesCapitalizeFirstLetter() {
        assertEquals("Source", WordUtils.capitalizeFirstLetter("source"));
        assertEquals("Source", WordUtils.capitalizeFirstLetter("sOuRcE"));
        assertEquals("", WordUtils.capitalizeFirstLetter(""));
        assertNull(WordUtils.capitalizeFirstLetter(null));
    }

    @Test
    public void testExtractWordInBetween() {
        String input = "Batch entry 0 delete from source where id=3 was aborted";
        String word = WordUtils.extractWordInBetween(input, "delete from ", " where").get();
        assertEquals("source", word);
    }

    @Test
    public void testExtractExtractorTextIncludePattern() {
        String input = "- DIV[id=\"maincontent\"]\n" +
            "- DIV[itemprop=\"articleBody\"]\n" +
            "- ARTICLE";
        List<String> words = WordUtils.toListOfStrings(input);
        assertEquals(3, words.size());
        assertEquals("[ DIV[id=\"maincontent\"]\n,  DIV[itemprop=\"articleBody\"]\n,  ARTICLE]", words.toString());
    }
}
