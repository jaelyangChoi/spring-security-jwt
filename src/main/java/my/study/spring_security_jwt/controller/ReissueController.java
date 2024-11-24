package my.study.spring_security_jwt.controller;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import my.study.spring_security_jwt.entity.RefreshToken;
import my.study.spring_security_jwt.jwt.JWTUtil;
import my.study.spring_security_jwt.repository.RefreshTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

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


        //DB에 저장되어 있는지 확인
        if (!refreshTokenRepository.existsByRefreshToken(refreshToken))
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);

        //새로운 토큰 생성
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwt("access", username, role, 100 * 60 * 10L);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, 100 * 60 * 60 * 24L);

        //Refresh 토큰 저장
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
        saveRefreshToken(username, newRefreshToken, 100 * 60 * 60 * 24L);

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

    private void saveRefreshToken(String username, String refreshToken, Long expiredMs) {
        Date expireDate = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setUsername(username);
        tokenEntity.setRefreshToken(refreshToken);
        tokenEntity.setExpiration(expireDate.toString());

        refreshTokenRepository.save(tokenEntity);
    }

}
