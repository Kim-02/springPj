package com.dasolsystem.core.announcement.dto;


import com.dasolsystem.core.enums.Role;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {

    private String title;
    private String username;
    private LocalDate creationDate;
    private Role selectrole;

    //TODO 파일 첨부 기능을 담은 필드 나중에 추가

    private String content;

}
