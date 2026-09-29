package vn.iotstar.exceptions;

import com.nimbusds.jose.JOSEException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.ParseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
        errorDetail.setProperty("access_denied_reason", "Authentication Failure: Email hoặc mật khẩu không chính xác");
        return errorDetail;
    }

    @ExceptionHandler(AccountStatusException.class)
    public ProblemDetail handleAccountStatusException(AccountStatusException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
        errorDetail.setProperty("access_denied_reason", "Tài khoản bị khóa, bị vô hiệu hóa hoặc đã hết hạn");
        return errorDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
        errorDetail.setProperty("access_denied_reason", "Bạn không có quyền truy cập vào tài nguyên này");
        return errorDetail;
    }

    /**
     * Bắt lỗi khi token sai cấu trúc hoặc không thể phân tích cú pháp bằng Nimbus SignedJWT.parse()
     */
    @ExceptionHandler(ParseException.class)
    public ProblemDetail handleParseException(ParseException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
        errorDetail.setProperty("access_denied_reason", "Token sai định dạng hoặc không hợp lệ (Nimbus ParseException)");
        return errorDetail;
    }

    /**
     * Bắt lỗi xử lý mã hóa hoặc xác thực chữ ký của Nimbus JOSE
     */
    @ExceptionHandler(JOSEException.class)
    public ProblemDetail handleJOSEException(JOSEException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
        errorDetail.setProperty("access_denied_reason", "Lỗi xử lý chữ ký hoặc giải mã token (Nimbus JOSEException)");
        return errorDetail;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(405), exception.getMessage());
        errorDetail.setProperty("access_denied_reason", "Phương thức HTTP không được hỗ trợ cho endpoint này");
        return errorDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
        errorDetail.setProperty("access_denied_reason", "Đã xảy ra lỗi nội bộ trên hệ thống");
        return errorDetail;
    }
}
