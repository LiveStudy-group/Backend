package org.livestudy.domain.title;


import jakarta.persistence.*;
import lombok.*;
import org.livestudy.domain.badge.Badge;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Title {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String  name; // 칭호 이름

    @Column
    private String description; // 칭호 설명

    @Column(length = 50, unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private TitleCode code;

    @Column(name = "title_icon", length = 1024)
    private String titleIcon; // 아이콘(URL)

    //달성 기준(구분)
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false)
    private ConditionType conditionType; // 조건 종류

    //달성 기준 수
    @Column(name = "condition_value", nullable = false)
    private Integer conditionValue; // 조건 값

    @Builder.Default
    @OneToMany(mappedBy = "title", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<UserTitle> userTitles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "badge")
    private Badge badge;

    private String imageUrl; // 이미지 주소

}
