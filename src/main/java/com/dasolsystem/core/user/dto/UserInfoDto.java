package com.dasolsystem.core.user.dto;

import com.dasolsystem.core.entity.Department;
import com.dasolsystem.core.entity.RoleCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoDto {

    //index
    private Long memberId;
    private String studentId;
    //바꿀 수 있는 정보
    private String enterYear;
    private String gender;
    private String email;
    private String phone;
    private String name;
    //바꾸지 못하는 정보
    private String password;
    private Boolean paidUser;
    private String roleName;
    private String department;


}
