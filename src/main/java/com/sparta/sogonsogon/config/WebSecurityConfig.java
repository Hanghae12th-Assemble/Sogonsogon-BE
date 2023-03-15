package com.sparta.sogonsogon.config;

import com.sparta.sogonsogon.jwt.JwtAuthFilter;
import com.sparta.sogonsogon.jwt.JwtUtil;
import com.sparta.sogonsogon.security.CustomAccessDeniedHandler;
import com.sparta.sogonsogon.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig implements WebMvcConfigurer {

    private final JwtUtil jwtUtil;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web) -> web.ignoring()
                //.requestMatchers(PathRequest.toH2Console())
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**")
                .antMatchers("/h2-console/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        //로그인 된 후 토큰없이 자동 인증되는 것을 방지
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //회원가입, 로그인,조회까지는 security 인증 없이도 가능함
        http.authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/docs").permitAll()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/api/member/signup").permitAll()
                .antMatchers("/api/member/login").permitAll()
                // .antMatchers(HttpMethod.GET, "/api/studies/**").permitAll()
                .anyRequest().authenticated()
                // JWT 인증/인가를 사용하기 위한 설정
                .and()
                .addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //Controller 단 전에 시큐리티에서 검사하므로 따로 Exceptionhandler가 필요하다
        http.exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint);
        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);
        return http.build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**") // 프로그램에서 제공하는 URL
                .allowedOrigins("*") // 요청을 허용할 출처를 명시, 전체 허용 (가능하다면 목록을 작성한다.
                .allowedMethods("*") // 어떤 메서드를 허용할 것인지 (GET, POST...)
                .allowedHeaders("Content-Type","Authorization") // 어떤 헤더들을 허용할 것인지
                .exposedHeaders("Content-Type","Authorization")
                .allowCredentials(false) // 쿠키 요청을 허용한다(다른 도메인 서버에 인증하는 경우에만 사용해야하며, true 설정시 보안상 이슈가 발생할 수 있다)
                .maxAge((long)3600 * 24 * 365); // preflight 요청에 대한 응답을 브라우저에서 캐싱하는 시간;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("http://3.37.146.173:8080");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}