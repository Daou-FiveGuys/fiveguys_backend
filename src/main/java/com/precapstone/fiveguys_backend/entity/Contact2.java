package com.precapstone.fiveguys_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.precapstone.fiveguys_backend.api.message.send.option.ChangeWord;
import com.precapstone.fiveguys_backend.api.message.send.option.Target;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contact2")
public class Contact2 {
    // 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contactId;

    // 주소록 명칭
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group2_id")
    @JsonBackReference
    private Group2 group2;

    // 주소록 전화번호
    @Column(nullable = false)
    private String telNum;

    // [*1*] 대체 단어
    @Builder.Default
    @Column(nullable = false)
    private String one = "";

    // [*2*] 대체 단어
    @Builder.Default
    @Column(nullable = false)
    private String two = "";

    // [*3*] 대체 단어
    @Builder.Default
    @Column(nullable = false)
    private String three = "";

    // [*4*] 대체 단어
    @Builder.Default
    @Column(nullable = false)
    private String four = "";

    // [*5*] 대체 단어
    @Builder.Default
    @Column(nullable = false)
    private String five = "";

    // [*6*] 대체 단어
    @Builder.Default
    @Column(nullable = false)
    private String six = "";

    // [*7*] 대체 단어
    @Builder.Default
    @Column(nullable = false)
    private String seven = "";

    // [*8*] 대체 단어
    @Builder.Default
    @Column(nullable = false)
    private String eight = "";

    public Contact2(Target target) {
        this.telNum = target.getToNumber(); // Target의 toNumber -> Contact2의 telNum
        this.name = target.getName(); // Target의 name -> Contact2의 name

        // Target의 changeWord를 Contact2의 one ~ eight 필드에 매핑
        ChangeWord changeWord = target.getChangeWord();
        if (changeWord != null) {
            this.one = changeWord.getVar1();
            this.two = changeWord.getVar2();
            this.three = changeWord.getVar3();
            this.four = changeWord.getVar4();
            this.five = changeWord.getVar5();
            this.six = changeWord.getVar6();
            this.seven = changeWord.getVar7();
            this.eight = "NONE";
        } else {
            // changeWord가 null인 경우, 기본값 설정
            this.one = "";
            this.two = "";
            this.three = "";
            this.four = "";
            this.five = "";
            this.six = "";
            this.seven = "";
            this.eight = "NONE";
        }
    }

}
