package com.dasolsystem.core.user.controller;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.config.excption.CodeFailException;
import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.guardian.SecurityGuardian;
import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.user.dto.DepartmentDto;
import com.dasolsystem.core.user.dto.PermissionChangeDto;
import com.dasolsystem.core.user.dto.UserEventParticipationResponseDto;
import com.dasolsystem.core.user.dto.UserProfileResponseDto;
import com.dasolsystem.core.user.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final SecurityGuardian securityGuardian;
    private final UserRepository userRepository;
    private final UserService userService;


    @GetMapping("/profile")
    public ResponseEntity<ResponseJson<?>> userProfile(HttpServletRequest request) {
        Claims loginClaim = securityGuardian.getServletTokenClaims(request);
        Member loginMember = userRepository.findByStudentId(loginClaim.getSubject()).orElseThrow(
                ()-> new AuthFailException(ApiState.ERROR_700,"로그인 맴버 정보가 일치하지 않습니다."));
        UserProfileResponseDto responseDto = userService.getUserProfile(loginMember);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .result(responseDto)
                        .message("success loading")
                        .build()
        );
    }

    @GetMapping("/event")
    public ResponseEntity<ResponseJson<?>> userEvent(HttpServletRequest request) {
        Claims loginClaim = securityGuardian.getServletTokenClaims(request);
        Member loginMember = userRepository.findByStudentId(loginClaim.getSubject()).orElseThrow(
                ()->new AuthFailException(ApiState.ERROR_700,"로그인 맴버 정보가 일치하지 않습니다.")
        );
        List<UserEventParticipationResponseDto> responseDto = userService.getUserEventParticipation(loginMember);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .result(responseDto)
                        .message("success loading")
                        .build()
        );
    }
    @PostMapping("/department/giving")
    public ResponseEntity<ResponseJson<?>> userGivingDepartment(@RequestBody DepartmentDto department, HttpServletRequest request) {
        if(!securityGuardian.userValidate(request,"Presidency")){
            throw new CodeFailException(ApiState.ERROR_101,"권한이 없습니다. 로그인 정보를 확인하세요");
        }
        DepartmentDto responseDto = userService.setUserDepartment(department.getStudentId(), department);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("역할 변경이 완료되었습니다. "+responseDto.getResult())
                .build()
        );

    }
    @DeleteMapping("/department/delete")
    public ResponseEntity<ResponseJson<?>> userDeleteDepartment(@RequestParam String studentId,HttpServletRequest request) {
        if(!securityGuardian.userValidate(request,"Presidency")){
            throw new CodeFailException(ApiState.ERROR_101,"권한이 없습니다. 로그인 정보를 확인하세요");
        }
        String result = userService.deleteUserDepartment(studentId);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("success "+result)
                        .build()
        );
    }
    @PostMapping("/permission/change")
    public ResponseEntity<ResponseJson<?>> userPermissionChange(@RequestBody PermissionChangeDto pcDto,HttpServletRequest request){
        if(!securityGuardian.userValidate(request,"Presidency")){
            throw new CodeFailException(ApiState.ERROR_101,"권한이 없습니다. 로그인 정보를 확인하세요");
        }
        String requesterId = securityGuardian.getServletTokenClaims(request).getSubject();
        String targetId = pcDto.getTargetStudentId();
        String reason = pcDto.getReason();
        String role = pcDto.getRole();
        userService.changeUserPermission(requesterId,reason,targetId,role);
        return ResponseEntity.ok(
                ResponseJson
                        .builder()
                        .status(200)
                        .message("변경 내역 저장 완료"+pcDto.getReason())
                .build()
        );
    }
}
