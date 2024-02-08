package hu.webuni.flights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import hu.webuni.tokenlib.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
		
	@Autowired
	JwtAuthFilter jwtAuthFilter;
	
	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(csrf ->
				csrf.disable()
			)
			.sessionManagement(sessionConfig ->
				sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)			
			.authorizeHttpRequests(auth ->
				auth				
				.requestMatchers(HttpMethod.GET, "/api/flight/**").hasAuthority("search")
				.anyRequest().permitAll()
			)
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
			.build()
			;		
	}

	
}
