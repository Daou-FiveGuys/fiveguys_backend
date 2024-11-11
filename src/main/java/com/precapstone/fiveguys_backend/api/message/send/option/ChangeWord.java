package com.precapstone.fiveguys_backend.api.message.send.option;

import lombok.Getter;
import lombok.Setter;

/**
 * 대체하고 싶은 내용을 지정할 수 있다.
 *
 * 각 항목은 [*n*] 문구에 치환된다.
 */
@Setter
@Getter
public class ChangeWord {
    private String var1 = "null";
    private String var2 = "null";
    private String var3 = "null";
    private String var4 = "null";
    private String var5 = "null";
    private String var6 = "null";
    private String var7 = "null";
}
