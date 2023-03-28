package eu.europa.ec.eurostat.wihp.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static eu.europa.ec.eurostat.wihp.util.EmailUtils.isValidEmailAddress;

public class EmailUtilsTests {

    @ParameterizedTest
    @ValueSource(strings = {
        "bond.james.bond@m7.uk.com",
        "bond_james_bond@m7.uk.com",
        "  bond_james_bond@m7.uk.com",
        "bond_james.bond@m7.uk.com  ",
        "  bond_james_bond@m7.uk.com  ",
        "abc@b.e.f"})
    public void positiveTestsEmail(String urlToTest) {
        Assertions.assertTrue(isValidEmailAddress(urlToTest));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "bond.james.bondm7.uk.com",
        "bond.james.bond@ m7.uk.com",
        "bond.james.bond @m7.uk.com",
        "bond.james.bond @ m7.uk.com",
        "bond.james.bond",
        "@m7.uk.com"})
    public void negativeTestsEmail(String urlToTest) {
        Assertions.assertFalse(isValidEmailAddress(urlToTest));
    }
}
