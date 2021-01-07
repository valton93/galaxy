package com.kran.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.kran.project.user.service.impl.CustomUserDetailsServiceImpl;
import com.kran.utility.encoder.CustomDESPasswordEncoder;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
    private CustomUserDetailsServiceImpl userDetailsService;
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
			.authorizeRequests()
	            .antMatchers(
	                    "/favicon.ico",
						"/webjars/**",
						"/images/**",
						"/css/**",
						"/public/**",
						"/open/**",
						"/login/**").permitAll()
	            .anyRequest().authenticated()
	        .and()
	            .formLogin()
	            .loginPage("/login").permitAll()
				.defaultSuccessUrl("/", true)
	        .and()
	            .logout()
	                .invalidateHttpSession(true)
	                .clearAuthentication(true)
	                .deleteCookies("JSESSONID")
	                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					.logoutSuccessUrl("/login?logout").permitAll();
	}
	@Override
	public void configure(WebSecurity web) throws Exception {
	    web.ignoring().antMatchers("/register","/saveNewScreeningSelf","/viewRegistration/**");
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new CustomDESPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(userDetailsService);
		auth.setPasswordEncoder(passwordEncoder());
		return auth;
	}
	
	public SimpleAuthenticationFilter authenticationFilter() throws Exception {
	    SimpleAuthenticationFilter filter = new SimpleAuthenticationFilter();
	    filter.setAuthenticationManager(authenticationManagerBean());
	    filter.setAuthenticationFailureHandler(failureHandler());
	    return filter;
	}
	
	public SimpleUrlAuthenticationFailureHandler failureHandler() {
        return new SimpleUrlAuthenticationFailureHandler("/login?error=true");
    }

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}
	
}