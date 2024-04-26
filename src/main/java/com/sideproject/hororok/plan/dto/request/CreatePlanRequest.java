package com.sideproject.hororok.plan.dto.request;

import com.sideproject.hororok.member.domain.Member;
import com.sideproject.hororok.plan.domain.Plan;
import com.sideproject.hororok.plan.domain.enums.MatchType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.sideproject.hororok.utils.FormatConverter.convertStringToLocalDate;
import static com.sideproject.hororok.utils.FormatConverter.convertStringToLocalTime;

@Getter
public class CreatePlanRequest {

    private String locationName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer minutes;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<String> keywords;

    public CreatePlanRequest(
            final String locationName, final BigDecimal latitude, final BigDecimal longitude,
            final Integer minutes, final LocalDate date, final LocalTime startTime,
            final LocalTime endTime, final List<String> keywords) {
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.minutes = minutes;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.keywords = keywords;
    }

    public Plan toEntity(final Member member, MatchType matchType) {
        return new Plan(
                member, locationName, date, startTime, endTime,
                minutes, matchType, false, false);
    }
}
