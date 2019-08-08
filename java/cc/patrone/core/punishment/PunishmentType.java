package cc.patrone.core.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PunishmentType {

    BAN("banned", true),
    KICK("kicked", false),
    MUTE("muted", true),
    BLACKLIST("blacklisted", false);

    private String pastTense;
    private boolean canBeTemp;
}
