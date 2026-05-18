package com.jh.chat.member.endpoint;

import com.jh.chat.member.application.usecase.JoinUseCase;
import com.jh.chat.member.application.usecase.LoginUseCase;
import com.jh.chat.member.application.usecase.request.JoinExecute;
import com.jh.chat.member.endpoint.payload.JoinPayload;
import com.jh.chat.member.endpoint.payload.LoginPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LoginController {

    private final JoinUseCase joinUseCase;
    private final LoginUseCase loginUseCase;

    @PostMapping("/api/v1/join")
    public ResponseEntity<JoinPayload.Response> join(@RequestBody JoinPayload.Request request) {
        joinUseCase.execute(request.toExecute());
        return ResponseEntity.ok(null);
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity<LoginPayload.Response> login(@RequestBody LoginPayload.Request request) {
        var result = loginUseCase.execute(request.loginId(), request.password());
        return ResponseEntity.ok(LoginPayload.Response.of(result.accessToken()));
    }
}
