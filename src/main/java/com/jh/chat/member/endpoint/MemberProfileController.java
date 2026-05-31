package com.jh.chat.member.endpoint;

import com.jh.chat.common.security.CurrentMemberProvider;
import com.jh.chat.member.application.usecase.FindMemberProfileUseCase;
import com.jh.chat.member.application.usecase.UpdateMemberProfileUseCase;
import com.jh.chat.member.endpoint.payload.MemberProfilePayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
@Tag(name = "Member Profile", description = "회원 프로필 API")
public class MemberProfileController {

    private final FindMemberProfileUseCase findMemberProfileUseCase;
    private final UpdateMemberProfileUseCase updateMemberProfileUseCase;
    private final CurrentMemberProvider currentMemberProvider;

    @Operation(summary = "내 프로필 조회", description = "회원 프로필 정보를 조회합니다.")
    @GetMapping("/profile")
    public ResponseEntity<MemberProfilePayload.Response> getProfile() {
        return ResponseEntity.ok(MemberProfilePayload.Response.of(
                findMemberProfileUseCase.execute(currentMemberProvider.getCurrentMemberId())
        ));
    }

    @Operation(summary = "내 프로필 수정", description = "회원 이름과 연락처를 수정합니다.")
    @PatchMapping("/profile")
    public ResponseEntity<MemberProfilePayload.Response> updateProfile(
            @RequestBody MemberProfilePayload.UpdateRequest request
    ) {
        return ResponseEntity.ok(
                MemberProfilePayload.Response.of(
                        updateMemberProfileUseCase.execute(request.toRequest(currentMemberProvider.getCurrentMemberId()))
                )
        );
    }
}
