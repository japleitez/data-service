package eu.europa.ec.eurostat.wihp.service.dto;

public enum SeleniumOptionsEnum {
    ALLOW(1),
    BLOCK(2);

    public final int value;

    SeleniumOptionsEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
