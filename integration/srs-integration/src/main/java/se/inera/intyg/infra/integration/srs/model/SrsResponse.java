package se.inera.intyg.infra.integration.srs.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class SrsResponse {
    private final int level;
    private final ImmutableList<String> atgarder;

    public SrsResponse(int level, List<String> atgarder) {
        this.level = level;
        this.atgarder = ImmutableList.<String> copyOf(atgarder);
    }

    public int getLevel() {
        return level;
    }

    public ImmutableList<String> getAtgarder() {
        return atgarder;
    }
}
