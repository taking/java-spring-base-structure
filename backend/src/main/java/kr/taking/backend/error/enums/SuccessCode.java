package kr.taking.backend.error.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

/**
 * <pre>
 * ClassName : ErrorCode
 * Type : enum
 * Descrption : 에러 코드, 에러 메시지를 포함하고 있는 enum입니다.
 * Related : ErrorResponse
 * </pre>
 */
@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SuccessCode {

    // Common
    OK(200, "Success");

    private final int status;
    private final String message;

    SuccessCode(final int status, final String message) {
        this.status = status;
        this.message = message;
    }
    }