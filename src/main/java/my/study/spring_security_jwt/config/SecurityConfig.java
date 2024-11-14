package my.study.spring_security_jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        //csrf disable : 세션을 stateless 상태로 관리하므로 필요 없음.
        http
                .csrf(auth -> auth.disable());

        //Form 로그인 방식 disable
        http
                .formLogin(auth -> auth.disable());

        //http 로그인 방식 disable
        http
                .httpBasic(auth -> auth.disable());

        //경로별 인가 작업
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/", "/join").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated());

        //세션 설정
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); //기본 추천 전략인 bcrypt 방식
    }
}
