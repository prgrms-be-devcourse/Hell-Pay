package com.programmers.heavenpay.member.controller;

import com.programmers.heavenpay.common.converter.ResponseConverter;
import com.programmers.heavenpay.common.dto.ResponseDto;
import com.programmers.heavenpay.common.dto.ResponseMessage;
import com.programmers.heavenpay.member.dto.request.MemberCreateRequest;
import com.programmers.heavenpay.member.dto.request.MemberUpdateRequest;
import com.programmers.heavenpay.member.dto.response.MemberCreateResponse;
import com.programmers.heavenpay.member.dto.response.MemberUpdateResponse;
import com.programmers.heavenpay.member.service.MemberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.HttpMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/members", produces = MediaTypes.HAL_JSON_VALUE)
public class MemberController {
    private final MemberService memberService;
    private final ResponseConverter responseConverter;

    private WebMvcLinkBuilder getLinkToAddress() {
        return linkTo(MemberController.class);
    }

    @ApiOperation("회원(Member) 신규 추가, 성공시 생성된 Member ID 반환")
    @PostMapping(consumes = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<ResponseDto> signUp(@Valid @RequestBody MemberCreateRequest request) {
        MemberCreateResponse response = memberService.create(
                request.getEmail(),
                request.getName(),
                request.getPhoneNumber(),
                request.getBirth(),
                request.getGender()
        );

        EntityModel<MemberCreateResponse> entityModel = EntityModel.of(
                response,
                getLinkToAddress().withSelfRel().withTitle(HttpMethod.POST.name()),
                getLinkToAddress().slash(response.getId()).withRel(MethodType.READ.getTypeStr()).withType(HttpMethod.GET.name()),
                getLinkToAddress().withRel(MethodType.READ_ALL.getTypeStr()).withType(HttpMethod.GET.name()),
                getLinkToAddress().slash(response.getId()).withRel(MethodType.UPDATE.getTypeStr()).withType(HttpMethod.PATCH.name()),
                getLinkToAddress().slash(response.getId()).withRel(MethodType.DELETE.getTypeStr()).withType(HttpMethod.DELETE.name())
        );

        return responseConverter.toResponseEntity(
                HttpStatus.OK,
                ResponseMessage.MEMBER_INSERT_SUCCESS,
                entityModel
        );
    }

    @ApiOperation("회원(Member) 단건 수정, 성공시 수정된 Member 정보 반환")
    @PatchMapping(value = "/{memberId}", consumes = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<ResponseDto> editInfo(@PathVariable Long memberId, @Valid @RequestBody MemberUpdateRequest request) {
        MemberUpdateResponse response = memberService.update(
                memberId,
                request.getEmail(),
                request.getName(),
                request.getPhoneNumber(),
                request.getBirth(),
                request.getGender()
        );

        EntityModel<MemberUpdateResponse> entityModel = EntityModel.of(
                response,
                getLinkToAddress().withRel(MethodType.CREATE.getTypeStr()).withType(HttpMethod.POST.name()),
                getLinkToAddress().slash(response.getId()).withRel(MethodType.READ.getTypeStr()).withType(HttpMethod.GET.name()),
                getLinkToAddress().withRel(MethodType.READ_ALL.getTypeStr()).withType(HttpMethod.GET.name()),
                getLinkToAddress().slash(response.getId()).withSelfRel().withType(HttpMethod.PATCH.name()),
                getLinkToAddress().slash(response.getId()).withRel(MethodType.DELETE.getTypeStr()).withType(HttpMethod.DELETE.name())
        );

        return responseConverter.toResponseEntity(
                HttpStatus.OK,
                ResponseMessage.MEMBER_UPDATE_SUCCESS,
                entityModel
        );
    }


}
