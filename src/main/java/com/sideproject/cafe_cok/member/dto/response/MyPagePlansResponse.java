package com.sideproject.cafe_cok.member.dto.response;

import com.sideproject.cafe_cok.plan.dto.PlanKeywordDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPagePlansResponse {

    private Integer page;
    private List<PlanKeywordDto> plans;

    public MyPagePlansResponse(final Integer page,
                               final List<PlanKeywordDto> plans) {
        this.page = page;
        this.plans = plans;
    }
}
