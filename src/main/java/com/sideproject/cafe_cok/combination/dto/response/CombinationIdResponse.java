package com.sideproject.cafe_cok.combination.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CombinationIdResponse {

    private Long combinationId;

    public static CombinationIdResponse of(final Long combinationId) {
        return CombinationIdResponse.builder()
                .combinationId(combinationId)
                .build();
    }
}
