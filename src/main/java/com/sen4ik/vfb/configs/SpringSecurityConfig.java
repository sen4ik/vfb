package com.sen4ik.vfb.configs;

import com.sen4ik.vfb.constants.Views;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Spring Security Configuration
 * http://docs.spring.io/spring-boot/docs/current/reference/html/howto-security.html
 * Switches off Spring Boot automatic security configuration
 *
 * @author Dusan
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	private final AccessDeniedHandler accessDeniedHandler;

	@Qualifier("currentUserDetailsServices")
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	public SpringSecurityConfig(AccessDeniedHandler accessDeniedHandler) {
		this.accessDeniedHandler = accessDeniedHandler;
	}

	/**
	 * HTTPSecurity configurer
	 * - roles ADMIN allow to access /admin/**
	 * - roles USER allow to access /user/** and /newPost/**
	 * - anybody can visit /, /home, /about, /registration, /error, /blog/**, /post/**, /h2-console/**
	 * - every other page needs authentication
	 * - custom 403 access denied handler
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		/*
		http
		    .csrf().ignoringAntMatchers("/telerivet/**").and()
			.authorizeRequests().antMatchers(HttpMethod.POST, "/telerivet/**").permitAll();
		*/

		http
			.csrf().ignoringAntMatchers("/twilio/**").and()
			.authorizeRequests().antMatchers(HttpMethod.POST, "/twilio/**").permitAll();

		http
			.authorizeRequests()
				.antMatchers("/", "/index", "/unsubscribe", "/contact_me", "/register", "/confirm", "/error", "/" + Views.accessDenied, "/js/**", "/bootstrap-datepicker/**", "/vendor/**", "/css/**", "/img/**").permitAll()
				.anyRequest().fullyAuthenticated()
				.and()
				.formLogin()
				.loginPage("/login")
				.failureUrl("/login?error")
				.usernameParameter("email")
				.defaultSuccessUrl("/admin/index")
				.permitAll().usernameParameter("email")
				.and()
				.logout()
				.logoutUrl("/logout")
				.deleteCookies("remember-me")
				.logoutSuccessUrl("/login?logout")
				.permitAll()
				.and().rememberMe()
				.and()
				.exceptionHandling().accessDeniedHandler(accessDeniedHandler)
				// Fix for H2 console
				.and().headers().frameOptions().disable();

		http.requiresChannel().requestMatchers(r->r.getHeader(("X-Forwarded-Proto")) != null).requiresSecure();
	}

	/**
	 * Authentication details
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
				.userDetailsService(userDetailsService)
				.passwordEncoder(new BCryptPasswordEncoder());
	}

}
