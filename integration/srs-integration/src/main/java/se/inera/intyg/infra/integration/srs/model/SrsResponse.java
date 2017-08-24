package se.inera.intyg.infra.integration.srs.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class SrsResponse {
    private final Integer level;
    private final ImmutableList<String> atgarder;
    private final String statistikBild;

    public SrsResponse(Integer level, List<String> atgarder, String statistikBild) {
        this.level = level;
        if (atgarder == null) {
            this.atgarder = null;
        } else {
            this.atgarder = ImmutableList.copyOf(atgarder);
        }
        this.statistikBild = statistikBild;
    }

    public Integer getLevel() {
        return level;
    }

    public ImmutableList<String> getAtgarder() {
        return atgarder;
    }

    public String getStatistikBild() {
        return statistikBild;
    }
}
