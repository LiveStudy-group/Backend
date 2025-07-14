package org.livestudy.domain.title;


import jakarta.persistence.*;
import lombok.*;

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
    private String  name;

    @Lob
    @Column
    private String description;

    @Column(name = "title_icon", length = 1024)
    private String titleIcon;

    //달성 기준(구분)
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false)
    private ConditionType conditionType;

    //달성 기준 수
    @Column(name = "condition_value", nullable = false)
    private Integer conditionValue;

    @Builder.Default
    @OneToMany(mappedBy = "title", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<UserTitle> userTitles = new ArrayList<>();
}
