package com.goormthon.hero_home.global.code.status;

import com.goormthon.hero_home.global.code.BaseErrorCode;
import com.goormthon.hero_home.global.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    //s3
    S3_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S34001", "사진 업로드에 실패했습니다."),
    S3_FORMAT(HttpStatus.BAD_REQUEST, "S34002", "잘못된 형식의 파일입니다."),
    S3_EMPTY_FILE(HttpStatus.BAD_REQUEST, "S34003", "업로드할 파일이 없습니다."),

    //user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "해당 유저를 찾을 수 없습니다."),

    //board
    BOARD_NOT_FOUNT(HttpStatus.NOT_FOUND, "BOARD404", "해당 글을 찾을 수 없습니다."),

    //review
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404", "해당 후원 현황 리뷰를 찾을 수 없습니다."),

    //reply
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "REPLY404", "해당 댓글을 찾을 수 없습니다."),
    REPLY_DELETE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "COMMON403", "댓글을 삭제할 권한이 없습니다."),

    //sponsorshipstatus
    DONATION_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "DONATION_STATUS_NOT_FOUND", "후원 현황 내역이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
                .message(message)
                .httpStatus(httpStatus)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
