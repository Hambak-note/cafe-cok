package com.sideproject.cafe_cok.combination.domain;

import com.sideproject.cafe_cok.keword.domain.Keyword;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(name = "combination_keywords")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CombinationKeyword {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "combinations_id")
    private Combination combination;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "keywords_id")
    private Keyword keyword;

    @Builder
    public CombinationKeyword(final Long id,
                              final Combination combination,
                              final Keyword keyword) {
        this.id = id;
        if(combination != null) changeCombination(combination);
        this.keyword = keyword;
    }

    public void changeCombination(final Combination combination) {
        this.combination = combination;
        combination.getCombinationKeywords().add(this);
    }
}
