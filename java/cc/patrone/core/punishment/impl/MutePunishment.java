package cc.patrone.core.punishment.impl;

import cc.patrone.core.punishment.Punishment;

import java.util.Date;

public class MutePunishment extends Punishment {

    public MutePunishment(String reason, String expire, Date expiry) {
        super(reason, expire, expiry);
    }
}
