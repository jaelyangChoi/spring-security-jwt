package my.study.spring_security_jwt.controller;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import my.study.spring_security_jwt.jwt.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("refresh"))
                refreshToken = cookie.getValue();
        }

        if (refreshToken == null) {
            return new ResponseEntity<>("refreshToken token null", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refreshToken token expired", HttpStatus.BAD_REQUEST);
        }

        //토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        if (!jwtUtil.getCategory(refreshToken).equals("refresh"))
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);


        //새로운 토큰 생성
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwt("access", username, role, 100 * 60 * 10L);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, 100 * 60 * 60 * 24L);

        //TODO : Refresh 토큰 블랙리스트 처리 로직

        response.setHeader("access", newAccessToken);
        response.addCookie(createCookie("refresh", newRefreshToken));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
//        cookie.setPath("/");
//        cookie.setSecure(true);
        cookie.setHttpOnly(true); //JS로 쿠키 접근 불가
        return cookie;
    }
}
