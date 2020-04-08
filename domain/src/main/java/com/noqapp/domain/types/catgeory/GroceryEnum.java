package com.noqapp.domain.types.catgeory;

import static java.util.stream.Collectors.toMap;

import com.noqapp.domain.types.BusinessTypeEnum;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * hitender
 * 4/1/20 2:22 PM
 */
public enum GroceryEnum {
    BNH("BNH", "Beauty & Health"),
    COS("COS", "Cosmetics"),
    HOL("HOL", "Hair Oil"),
    HED("HED", "Henna - Dye"),
    MED("MED", "Medicines"),
    SMP("SMP", "Shampoo"),
    SOP("SOP", "Soaps - Pastes"),
    BEV("BEV", "Beverages"),
    COF("COF", "Coffee"),
    DNK("DNK", "Drinks"),
    HNK("HNK", "Health Drinks"),
    JUC("JUC", "Juice Mixes"),
    TEA("TEA", "Tea"),
    CNF("CNF", "Confectioneries"),
    BCK("BCK", "Biscuits - Cakes"),
    CDY("CDY", "Candy"),
    RUS("RUS", "Cookies - Rusks"),
    DES("DES", "Desserts - Sweets"),
    SWT("SWT", "Fresh Sweets"),
    JAM("JAM", "Jams - Jello"),
    COE("COE", "Cooking Essentials"),
    CAV("CAV", "Canned Vegetables"),
    COO("COO", "Cooking Oils"),
    COP("COP", "Cooking Pastes"),
    LOS("LOS", "Loose Spices"),
    SAF("SAF", "Saffron"),
    SPM("SPM", "Spice Mixes"),
    EMS("EMS", "E-Miscellaneous"),
    APP("APP", "Appliances"),
    COL("COL", "Colors - Essences"),
    CUS("CUS", "Custard - Sugar"),
    GFT("GFT", "Gifts"),
    INC("INC", "Incenses"),
    POI("POI", "Pooja Items"),
    FFO("FFO", "Frozen Foods"),
    BRD("BRD", "Breads"),
    DAI("DAI", "Dairy Products"),
    ENT("ENT", "Entrees - Dinners"),
    ICE("ICE", "Ice-Creams"),
    NVG("NVG", "Non-Vegetarian"),
    NOS("NOS", "North Snacks"),
    SOS("SOS", "South Snacks"),
    VEG("VEG", "Vegetables"),
    GFO("GFO", "GOURMET FOOD"),
    GCO("GCO", "Gourmet Cookies"),
    GKI("GKI", "Gourmet Kitchen"),
    GOA("GOA", "Gourmet Oats"),
    GPA("GPA", "Gourmet Papad"),
    GPI("GPI", "Gourmet Pickles"),
    GPW("GPW", "Gourmet Powders"),
    GSN("GSN", "Gourmet Snacks"),
    GSP("GSP", "Gourmet Spices"),
    GSW("GSW", "Gourmet Sweets"),
    GRO("GRO", "Groceries"),
    FIS("FIS", "Fish - Meat"),
    FRV("FRV", "Fresh Vegetables"),
    GHE("GHE", "Ghee"),
    JAG("JAG", "Jaggery"),
    MUK("MUK", "Mukhwas"),
    PAP("PAP", "Papad"),
    POL("POL", "Poultry"),
    TAM("TAM", "Tamarind"),
    VER("VER", "Vermicelli"),
    INS("INS", "Instant Food"),
    CUT("CUT", "Chutneys - Sauces"),
    FRS("FRS", "Fresh Snacks"),
    INF("INF", "Infant Food"),
    INM("INM", "Instant Mixes"),
    NOO("NOO", "Noodles"),
    PIK("PIK", "Pickles"),
    RTE("RTE", "Ready To Eat"),
    SNK("SNK", "Snacks"),
    SOU("SOU", "Soups"),
    STA("STA", "Staples"),
    ATT("ATT", "Atta"),
    BES("BES", "Besan"),
    DAL("DAL", "Dals/Lentils"),
    FLO("FLO", "Flours"),
    MAM("MAM", "Mamra - Poha"),
    NUT("NUT", "Nuts - Dry Fruits"),
    RIC("RIC", "Rice"),
    SAB("SAB", "Sabudana"),
    SOY("SOY", "Soya-Vadi"),
    OTH("OTH", "Other - Miscellaneous");

    private final String description;
    private final String name;

    GroceryEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public static List<GroceryEnum> asList() {
        return Stream.of(GroceryEnum.values())
            .sorted(Comparator.comparing(GroceryEnum::getDescription))
            .collect(Collectors.toList());
    }

    public static List<String> asListOfDescription() {
        List<String> a = new LinkedList<>();
        for (GroceryEnum category : GroceryEnum.values()) {
            a.add(category.description);
        }

        return a;
    }

    public static Map<String, String> asMapWithNameAsKey() {
        List<GroceryEnum> groceryEnums = Stream.of(GroceryEnum.values())
            .sorted(Comparator.comparing(GroceryEnum::getDescription))
            .collect(Collectors.toList());

        /* https://javarevisited.blogspot.com/2017/07/how-to-sort-map-by-keys-in-java-8.html */
        return groceryEnums.stream()
            .collect(toMap(GroceryEnum::getName, GroceryEnum::getDescription, (e1, e2) -> e2, LinkedHashMap::new));
    }

    public static Map<String, String> asMapWithDescriptionAsKey() {
        List<GroceryEnum> groceryEnums = Stream.of(GroceryEnum.values())
            .sorted(Comparator.comparing(GroceryEnum::getDescription))
            .collect(Collectors.toList());

        /* https://javarevisited.blogspot.com/2017/07/how-to-sort-map-by-keys-in-java-8.html */
        return groceryEnums.stream()
            .collect(toMap(GroceryEnum::getDescription, GroceryEnum::getName, (e1, e2) -> e2, LinkedHashMap::new));
    }

    @Override
    public String toString() {
        return description;
    }
}
