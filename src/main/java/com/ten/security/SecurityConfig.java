package com.ten.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import com.ten.security.handlers.CustomAuthenticationFailureHandler;
import com.ten.security.handlers.CustomAuthenticationSuccessHandler;
import com.ten.security.service.UserDetailsServiceImpl;

@Configuration
@ComponentScan(basePackages = "com.ten")
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsServiceImpl authenticationService;//чекает юзера в бд по его логину есть или нет
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;//положиельный хендлер для залогиненных
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;//для не залогиненных

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl authenticationService,
                          CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler,
                          CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.authenticationService = authenticationService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }

    @Override
    protected void configure(HttpSecurity http) {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);//Set whether the configured encoding of this filter is supposed to override existing request and response encodings.
        try {
            http.csrf().disable().addFilterBefore(filter, CsrfFilter.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //hgh
            http.authorizeRequests()
                    //позволяет зарестирктить достур
                    .antMatchers("/user/**")//указываю для какого юрла хочу настроить доступ
                    .hasAnyAuthority("ROLE_USER")//юзер с ролью юзер может войти на юрл выше
                    .antMatchers("/admin/**")
                    //.permitAll()
                    .hasAnyAuthority("ROLE_ADMIN")
                    .and()
                    .formLogin()//регистрируем страницу с формой логина
                    .loginPage("/login")//имя джспи страницы куда направить по дефолту при запуске
                    .loginProcessingUrl("/loginpr")//сабмитит наши креденшиалы
                    //указываем action с формы логина/login-processing-url — задает значение action у form при котором Spring Security понимает,
                    // что нужно проверять пользователя согласно настройкам.
                    //сообщает Spring Security обрабатывать предоставленные учетные данные при отправке указанного пути
                    // и, по умолчанию, перенаправляет пользователя обратно пользователю страницы
                    .successHandler(customAuthenticationSuccessHandler)//вот это не тригеритс после того как loadUserByUsername отработал
                    .failureHandler(customAuthenticationFailureHandler)
                    .usernameParameter("login")//принимает логин введенный в форму вот в кавычках login это парметр на форме login.html
                    .passwordParameter("password")
                    .and().exceptionHandling().accessDeniedPage("/error");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("{noop}user").roles("USER");
        auth.inMemoryAuthentication().withUser("admin").password("{noop}admin").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("ad").password("{noop}ad").roles("ADMIN");
    }*/

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        //PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        //User.withUsername("admin").password("{noop}admin").roles("ADMIN").build();
        //The name of the configureGlobal method is not important. However,
        // it is important to only configure AuthenticationManagerBuilder in a class annotated with either @EnableWebSecurity
        try {
            auth.
                    userDetailsService(authenticationService).passwordEncoder(encoder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}