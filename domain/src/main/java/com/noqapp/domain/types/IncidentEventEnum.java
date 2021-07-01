package com.noqapp.domain.types;

import static com.noqapp.domain.types.BrahmandEnum.Event;
import static com.noqapp.domain.types.BrahmandEnum.Human;
import static com.noqapp.domain.types.BrahmandEnum.Nature;
import static com.noqapp.domain.types.BrahmandEnum.Personal;
import static com.noqapp.domain.types.BrahmandEnum.Utilities;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * In Your Neighborhood. NoQueue Community.
 * hitender
 * 5/17/21 9:08 AM
 */
public enum IncidentEventEnum {
    //Nature -- 15km radius
    AVLC("AVLC", "Avalanche", "N", Nature, 15),
    CLDB("CLDB", "Cloudburst", "N", Nature, 15),
    CYCL("CYCL", "Cyclone", "N", Nature, 15),
    ERTQ("ERTQ", "Earthquake", "N", Nature, 15),
    FLOD("FLOD", "Flooding", "N", Nature, 15),
    FORF("FORF", "ForestFire", "N", Nature, 15),
    HAIL("HAIL", "Hail", "N", Nature, 15),
    LIGT("LIGT", "Lightning", "N", Nature, 15),
    RAIN("RAIN", "Torrential Rain", "N", Nature, 15),
    SNOW("SNOW", "Snow", "N", Nature, 15),
    STRM("STRM", "Storm", "N", Nature, 15),
    VOLC("VOLC", "Volcano", "N", Nature, 15),

    //Utilities
    ELEC("ELEC", "No Electricity", "U", Utilities, 5),
    WATR("WATR", "No Water", "U", Utilities, 5),
    PHON("PHON", "Phone Disrupted", "U", Utilities, 5),
    INTR("INTR", "No Internet", "U", Utilities, 5),

    //Human - 15km radius
    VACD("ACDN", "Accident", "H", Human, 15),
    FIRE("FIRE", "Fire", "H", Human, 15),
    KIDN("KIDN", "Kidnapped", "H", Human, 15),
    LYNC("LYNC", "Lynch", "H", Human, 15),
    MURD("MURD", "Murder", "H", Human, 15),
    RIOT("RIOT", "Riot", "H", Human, 15),
    STON("STON", "Stone Pelting", "H", Human, 15),
    SOSP("SOSP", "SOS", "H", Human, 0),

    //Personal - 3km radius
    ASLT("ASLT", "Assault", "P", Personal, 3),
    BLDN("BLDN", "Blood Needed", "P", Personal, 3),
    BRIB("BRIB", "Bribe", "P", Personal, 3),
    BURG("BURG", "Burglary", "P", Personal, 3),
    HLPN("HLPN", "Help Needed", "P", Personal, 3),
    LOST("LOST", "Lost", "P", Personal, 3),
    ROBD("ROBD", "Robbed", "P", Personal, 3),
    SCAM("SCAM", "Scam", "P", Personal, 3),

    //Paid Event - 15km radius
    BLDD("BLDD", "Blood Donation", "E", Event, 15),
    CLUB("CLUB", "Club", "E", Event, 15),
    GATH("GATH", "Gather", "E", Event, 15),
    INFO("INFO", "Information", "E", Event, 15),
    SALE("SALE", "Sale", "E", Event, 15),
    SHOW("SHOW", "Shows", "E", Event, 15),
    WISH("WISH", "Wish", "E", Event, 15),
    YOGA("YOGA", "Yoga", "E", Event, 15);

    private final String description;
    private final String name;
    private final String appendTopic;
    private final BrahmandEnum brahmand;
    private final double distanceToPropagateInformation;

    IncidentEventEnum(String name, String description, String appendTopic, BrahmandEnum brahmand, double distanceToPropagateInformation) {
        this.name = name;
        this.description = description;
        this.appendTopic = appendTopic;
        this.brahmand = brahmand;
        this.distanceToPropagateInformation = distanceToPropagateInformation;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAppendTopic() {
        return appendTopic;
    }

    public BrahmandEnum getBrahmand() {
        return brahmand;
    }

    public double getDistanceToPropagateInformation() {
        return distanceToPropagateInformation;
    }

    public static List<IncidentEventEnum> asList() {
        List<IncidentEventEnum> list = Stream.of(IncidentEventEnum.values())
            .sorted(Comparator.comparing(IncidentEventEnum::getBrahmand))
            .collect(Collectors.toList());

        /* On hold for a while. */
        list.remove(LYNC);
        list.remove(RIOT);
        list.remove(SOSP);
        list.remove(STON);
        return list;
    }

    @Override
    public String toString() {
        return description;
    }
}
