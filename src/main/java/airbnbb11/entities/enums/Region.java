package airbnbb11.entities.enums;

import lombok.Getter;

@Getter
public  enum Region {
    OSH("ОШ"),
    CHUI("ЧУЙ"),
    JALAL_ABAD("ЖАЛАЛ-АБАД"),
    NARYN("НАРЫН"),
    TALAS("ТАЛАС"),
    ISSYK_KUL("ИССЫК-КУЛ"),
    BATKEN("БАТКЕН"),
    BISHKEK("БИШКЕК");

    private final String region;

    Region(String region) {
        this.region = region;
    }

    public static Region getRegion(String region) {
        for (Region r : Region.values()) {
            if (r.getRegion().equals(region)) {
                return r;
            }
        }
        return null;
    }


}
