package com.dasolsystem.core.user.controller;

import com.dasolsystem.config.excption.AuthFailException;
import com.dasolsystem.config.excption.CodeFailException;
import com.dasolsystem.core.auth.repository.UserRepository;
import com.dasolsystem.core.entity.Member;
import com.dasolsystem.core.enums.ApiState;
import com.dasolsystem.core.guardian.SecurityGuardian;
import com.dasolsystem.core.handler.ResponseJson;
import com.dasolsystem.core.user.dto.*;
import com.dasolsystem.core.user.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final SecurityGuardian securityGuardian;
    private final UserRepository userRepository;
    private final UserService userService;

    //유저 정보를 불러오는 기능
    @GetMapping("/profile")
    public ResponseEntity<ResponseJson<?>> userProfile(HttpServletRequest request) {
        Claims loginClaim = securityGuardian.getServletTokenClaims(request);
        Member loginMember = userRepository.findByStudentId(loginClaim.getSubject()).orElseThrow(
                ()-> new AuthFailException(ApiState.ERROR_700,"로그인 맴버 정보가 일치하지 않습니다."));
        UserInfoDto responseDto = userService.getUserProfile(loginMember);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .result(responseDto)
                        .message("success loading")
                        .build()
        );
    }
    //유저 정보 변경 기능 로그인된 상태에서 유저 페이지를 눌러 진입할 수 있음
    @PostMapping("/profile/change")
    public ResponseEntity<ResponseJson<?>> changeUserProfile(HttpServletRequest request,@RequestBody UserInfoDto userInfoDto) {
        Claims loginClaim = securityGuardian.getServletTokenClaims(request);
        userInfoDto.setStudentId(loginClaim.getSubject());
        userService.changeUserInfo(userInfoDto);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message("유저 정보 변경 완료")
                .build()
        );
    }

    /**
     * 로그인 한 유저의 이벤트 참여 기록을 가져온다. 참여 기록에는 이벤트 입금자명과 납부 여부가 나온다.
     * @param request
     * @return
     */
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
    //사용자가 속한 이벤트에서 나갈 수 있는 기능
    @PostMapping("/event/leave/{eventId}")
    public ResponseEntity<ResponseJson<?>> eventLeave(HttpServletRequest request,@PathVariable Long eventId) {
        Claims loginClaim = securityGuardian.getServletTokenClaims(request);
        String leaveName = userService.userLeaveEvent(loginClaim.getSubject(), eventId);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .message(leaveName+"이벤트 취소가 완료되었습니다.")
                .build()
        );
    }

    /**
     * 회장 이상 권한을 가지고 있어야 함. 유저의 부서를 이동
     * @param department 이동할 부서와 이동할 학번을 입력함
     */
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

    /**
     * 회장 이상 권한 필요
     * 학번을 입력하고 그 학번에 해당하는 부서를 삭제함
     * ////////사용안함//////////
     * @param studentId
     * @param request
     * @return
     */
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

    /**
     * 권한을 변경함 회장 이상의 권한이 필요함
     * @param pcDto 바꾸려는 학생의 학번과 이유, 바꾸려는 역할의 코드를 입력해야함
     * @param request
     * @return
     */
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
    //권한 변경 기록을 가져온다
    @GetMapping("/permission/get/all")
    public ResponseEntity<ResponseJson<?>> userPermissionGetAll(HttpServletRequest request){
        if(!securityGuardian.userValidate(request,"Presidency")){
            throw new CodeFailException(ApiState.ERROR_101,"권한이 없습니다. 로그인 정보를 확인하세요");
        }
        String loginId = securityGuardian.getServletTokenClaims(request).getSubject();
        List<PermissionChangeLogDto> resDto = userService.getPermissionChangeLog(loginId);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .result(resDto)
                        .build()
        );
    }

    /**
     * xlsx파일 형태의 첫 줄에 학번을 넣고 돌리면 미납자와 납부자를 구분해준다.
     *
     * @param file xlsx파일
     * @return xslx파일
     */
    @PostMapping("/search/paiduser/xlsx")
    public ResponseEntity<byte[]> returnPaidUserXlsx(@RequestPart MultipartFile file) throws IOException {
        byte[] resBody = userService.buildResponseXlsx(file);

        // 원본 파일명 확장자 제거 후 새 이름 만들기
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.endsWith(".xlsx")) {
            originalName = originalName.substring(0, originalName.length() - 5);
        }
        String downloadName = "학회비납부자_" + originalName + ".xlsx";

        ContentDisposition cd = ContentDisposition.builder("attachment")
                .filename(downloadName, StandardCharsets.UTF_8) // 핵심
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, cd.toString())
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resBody.length)
                .body(resBody);
    }

    /**
     * 학번을 받아서 만약 있다면 해당 입금 정보를 봔한하고 없다면 errorMessage로 미납자입니다. 를 반환
     * @param stdId 학번입력
     * @return 결과 반환
     */
    @PostMapping("/search/paiduser/find")
    public ResponseEntity<ResponseJson<?>> findPaidUser(@RequestBody String stdId){
        UserYNResponseDto responseDto = userService.getUserYN(stdId);
        return ResponseEntity.ok(
                ResponseJson.builder()
                        .status(200)
                        .result(responseDto)
                        .build()
        );
    }
}
