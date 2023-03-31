package com.sparta.sogonsogon.config;

import com.sparta.sogonsogon.jwt.JwtAuthFilter;
import com.sparta.sogonsogon.jwt.JwtUtil;
//import com.sparta.sogonsogon.member.oauth.service.CustomOAuth2MemberService;
import com.sparta.sogonsogon.security.CustomAccessDeniedHandler;
import com.sparta.sogonsogon.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET) //붐비 붐비에 추가 되어 있는 내용
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
//        http.cors().configurationSource(corsConfigurationSource());
        http.csrf().disable();

        //로그인 된 후 토큰없이 자동 인증되는 것을 방지
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //회원가입, 로그인,조회까지는 security 인증 없이도 가능함
        http.authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
//                .antMatchers("/docs").permitAll()
//                .antMatchers("/api/**").permitAll()
            .antMatchers("/api/member/signup").permitAll()
            .antMatchers("/api/member/login").permitAll()
            // 멤버조회
            .antMatchers("/api/member/**").permitAll()
            // 라디오조회
            .antMatchers(HttpMethod.GET, "/api/radios/**").permitAll()
                .antMatchers("/webSocket").permitAll()
            .anyRequest().authenticated()
            // JWT 인증/인가를 사용하기 위한 설정
            .and()
            .addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
               .oauth2Login();
//                .logout()//oauth2 관련 내용 추가 (89번째 줄까지)
//                .logoutSuccessUrl("/")
//                .and()
//                .oauth2Login()
//                .defaultSuccessUrl("/login-success")
//                .userInfoEndpoint()
//                .userService(customOAuth2MemberService);

        //Controller 단 전에 시큐리티에서 검사하므로 따로 Exceptionhandler가 필요하다
        http.exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint);
        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);
        return http.build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedOriginPatterns("*") // 허용되는 출처 패턴을 사용하여 와일드카드(*) 지정
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(true)
                .maxAge((long) 3600 * 24 * 365);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(Arrays.asList("*")); // 허용되는 출처 패턴을 사용하여 와일드카드(*) 지정
        config.setAllowedMethods(Arrays.asList("*"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.addExposedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}