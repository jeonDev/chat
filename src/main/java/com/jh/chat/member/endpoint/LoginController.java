package com.jh.chat.member.endpoint;

import com.jh.chat.member.application.usecase.JoinUseCase;
import com.jh.chat.member.application.usecase.LoginUseCase;
import com.jh.chat.member.endpoint.payload.JoinPayload;
import com.jh.chat.member.endpoint.payload.LoginPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "Member", description = "회원 API")
public class LoginController {

    private final JoinUseCase joinUseCase;
    private final LoginUseCase loginUseCase;

    @Operation(summary = "회원가입", description = "로그인 ID, 비밀번호, 이름으로 회원을 생성합니다.")
    @PostMapping("/api/v1/join")
    public ResponseEntity<Void> join(@RequestBody JoinPayload.Request request) {
        joinUseCase.execute(request.toExecute());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인", description = "로그인 ID와 비밀번호로 액세스 토큰을 발급합니다.")
    @PostMapping("/api/v1/login")
    public ResponseEntity<LoginPayload.Response> login(@RequestBody LoginPayload.Request request) {
        var result = loginUseCase.execute(request.loginId(), request.password());
        return ResponseEntity.ok(LoginPayload.Response.of(result.accessToken()));
    }
}
