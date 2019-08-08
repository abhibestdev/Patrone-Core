package cc.patrone.core.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class Punishment {

    private String reason;
    private String expire;
    private Date expiry;

}
