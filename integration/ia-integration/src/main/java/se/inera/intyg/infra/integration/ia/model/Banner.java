/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.intyg.infra.integration.ia.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

public class Banner implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private UUID id;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = FORMAT)
    private LocalDateTime createdAt;
    private Application application;
    private String message;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = FORMAT)
    private LocalDateTime displayFrom;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = FORMAT)
    private LocalDateTime displayTo;
    private BannerPriority priority;

    public Banner() {
    }

    public Banner(UUID id, LocalDateTime createdAt, Application application, String message, LocalDateTime displayFrom,
                  LocalDateTime displayTo, BannerPriority priority) {
        this.id = id;
        this.createdAt = createdAt;
        this.application = application;
        this.message = message;
        this.displayFrom = displayFrom;
        this.displayTo = displayTo;
        this.priority = priority;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Application getApplication() {
        return application;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDisplayFrom() {
        return displayFrom;
    }

    public LocalDateTime getDisplayTo() {
        return displayTo;
    }

    public BannerPriority getPriority() {
        return priority;
    }
}
