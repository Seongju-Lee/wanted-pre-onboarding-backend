package preonboarding.board.configuration.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Getter
public enum TokenError {
    NO_SIGNATURE(HttpServletResponse.SC_UNAUTHORIZED, "토큰 서명 검증에 실패했습니다."),
    EXPIRED(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID(HttpServletResponse.SC_UNAUTHORIZED, "올바르지 않은 형식의 토큰입니다."),
    MALFORMED(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    MISSING(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 없습니다.");

    private final int status;
    private final String message;
}
