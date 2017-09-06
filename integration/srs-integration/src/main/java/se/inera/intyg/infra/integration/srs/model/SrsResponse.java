package se.inera.intyg.infra.integration.srs.model;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class SrsResponse {
    private final Integer predictionLevel;
    private final String predictionDescription;
    private final ImmutableList<String> atgarderObs;
    private final ImmutableList<String> atgarderRek;
    private final String statistikBild;

    public SrsResponse(Integer level, String description, List<String> atgarderObs, List<String> atgarderRek, String statistikBild) {
        this.predictionLevel = level;
        this.predictionDescription = description;
        if (atgarderObs == null) {
            this.atgarderObs = null;
        } else {
            this.atgarderObs = ImmutableList.copyOf(atgarderObs);
        }
        if (atgarderRek == null) {
            this.atgarderRek = null;
        } else {
            this.atgarderRek = ImmutableList.copyOf(atgarderRek);
        }
        this.statistikBild = statistikBild;
    }

    public Integer getPredictionLevel() {
        return predictionLevel;
    }

    public String getPredictionDescription() {
        return predictionDescription;
    }

    public ImmutableList<String> getAtgarderObs() {
        return atgarderObs;
    }

    public ImmutableList<String> getAtgarderRek() {
        return atgarderRek;
    }

    public String getStatistikBild() {
        return statistikBild;
    }
}
