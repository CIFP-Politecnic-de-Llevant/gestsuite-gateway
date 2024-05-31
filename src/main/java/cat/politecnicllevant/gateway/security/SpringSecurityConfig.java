package cat.politecnicllevant.gateway.security;

import cat.politecnicllevant.gateway.dto.RolDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity
public class SpringSecurityConfig {

    //@Autowired
    //private AuthenticationManagerJwt authenticationManagerJwt;

    @Autowired
    private JwtAuthenticationFilter authenticationFilter;


    @Value("${spring.cors.allowed}")
    private String allowedCors;

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {
        List<String> allRols = Arrays.stream(RolDto.values()).map(r -> r.name()).collect(Collectors.toList());
        return http
                //.authenticationManager(authenticationManagerJwt)
                .authorizeExchange()
                //General
                .pathMatchers("**/error").hasAnyAuthority(allRols.toArray(new String[0]))

                //Públic

                //Actuator
                .pathMatchers("/actuator/health").permitAll()
                .pathMatchers("/api/core/actuator/health").permitAll()
                .pathMatchers("/api/professoratmanager/actuator/health").permitAll()

                //Core - Auth
                .pathMatchers("/api/core/auth/google/login").permitAll()
                .pathMatchers("/api/core/auth/profile/rol").permitAll()
                .pathMatchers("/api/core/public/**").permitAll()
                .pathMatchers("/api/core/external/gsuite/sendemailattachment").permitAll()
                .pathMatchers("/api/convalidacions/public/**").permitAll()
                .pathMatchers("/api/professoratmanager/public/**").permitAll()

                //Core - Administrador
                .pathMatchers("/api/core/administrator/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name())

                //Core - Centre
                .pathMatchers("/api/core/centre/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(), RolDto.DIRECTOR.name())
                //.pathMatchers("/api/core/centre/password-inicial").hasAnyAuthority(allRols.toArray(new String[0]))

                //Core - Calendari
                .pathMatchers("/api/core/calendari/llistat").hasAnyAuthority(RolDto.ADMINISTRADOR.name(), RolDto.DIRECTOR.name())

                //Core - Grups de correu
                .pathMatchers("/api/core/grupcorreu/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(), RolDto.DIRECTOR.name(), RolDto.CAP_ESTUDIS.name())

                //Core - Sincronització
                .pathMatchers("/api/core/sync/reassignarGrups",
                        "/api/core/sync/reassignarGrupsProfessors",
                        "/api/core/sync/reassignarGrupsAlumnes",
                        "/api/core/sync/reassignarGrupsCorreuGSuiteToDatabase").hasAnyAuthority(RolDto.ADMINISTRADOR.name(), RolDto.DIRECTOR.name(), RolDto.CAP_ESTUDIS.name())
                .pathMatchers("/api/core/sync/sincronitza").hasAnyAuthority(RolDto.ADMINISTRADOR.name())
                .pathMatchers("/api/core/sync/uploadfile").hasAnyAuthority(RolDto.ADMINISTRADOR.name(), RolDto.DIRECTOR.name())

                //Core - Usuaris
                .pathMatchers("/api/core/usuaris/desaRols").hasAnyAuthority(RolDto.ADMINISTRADOR.name())
                .pathMatchers("/api/core/usuaris/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(), RolDto.DIRECTOR.name(), RolDto.CAP_ESTUDIS.name())
                .pathMatchers("/api/core/usuari/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(), RolDto.DIRECTOR.name(), RolDto.CAP_ESTUDIS.name())

                //Core - Curs
                .pathMatchers("/api/core/curs/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(), RolDto.DIRECTOR.name(), RolDto.CAP_ESTUDIS.name())

                //Core - Grups
                .pathMatchers("/api/core/grup/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(), RolDto.DIRECTOR.name(), RolDto.CAP_ESTUDIS.name())

                //Core - Departament
                .pathMatchers("/api/core/departament/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(), RolDto.DIRECTOR.name(), RolDto.CAP_ESTUDIS.name())

                //Core - Llistats
                .pathMatchers("/api/core/google/sheets/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(), RolDto.DIRECTOR.name(), RolDto.CAP_ESTUDIS.name())

                //Convalidacions - All
                .pathMatchers("/api/convalidacions/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(), RolDto.DIRECTOR.name(), RolDto.CAP_ESTUDIS.name())

                //Professorat Manager - All
                .pathMatchers("/api/professoratmanager/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(), RolDto.DIRECTOR.name(), RolDto.CAP_ESTUDIS.name(), RolDto.WEB.name())

                //Reserves - All
                .pathMatchers("/api/reserves/**").hasAnyAuthority(RolDto.PROFESSOR.name(), RolDto.ADMINISTRADOR.name())

                //Sheet Perser - All
                .pathMatchers("/api/sheetparser/**").hasAnyAuthority(RolDto.PROFESSOR.name(), RolDto.ADMINISTRADOR.name())

                //Gestor Documental
                .pathMatchers("/api/gestordocumental/**").permitAll()


                .anyExchange().authenticated()
                .and()
                .addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .csrf().disable()
                .cors()
                .configurationSource(corsConfigurationSource())
                .and()
                .build();
    }

//    @Bean
//    public SecurityWebFilterChain configure(ServerHttpSecurity http){
//        List<String> allRols = Arrays.stream(RolDto.values()).map(r -> r.name()).collect(Collectors.toList());
//
//        return http
//                .authenticationManager(authenticationManagerJwt)
//                .authorizeExchange()
//                //General
//                .pathMatchers("**/error").hasAnyAuthority(allRols.toArray(new String[0]))
//
//                //Públic
//                .pathMatchers("/api/core/auth/google/login").permitAll()
//                .pathMatchers("/api/core/auth/profile/rol").permitAll()
//                .pathMatchers("/api/core/public/**").permitAll()
//                .pathMatchers("/api/convalidacions/public/**").permitAll()
//
//                //Administrador
//                .pathMatchers("/api/core/administrator/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name())
//
//                //Centre
//                .pathMatchers("/api/core/centre/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(),RolDto.DIRECTOR.name())
//
//                //Calendari
//                .pathMatchers("/api/core/calendari/llistat").hasAnyAuthority(RolDto.ADMINISTRADOR.name(),RolDto.DIRECTOR.name())
//
//                //Grups de correu
//                .pathMatchers("/api/core/grupcorreu/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(),RolDto.DIRECTOR.name(),RolDto.CAP_ESTUDIS.name())
//
//                //Sincronització
//                .pathMatchers("/api/core/sync/reassignarGrups",
//                        "/api/core/sync/reassignarGrupsProfessors",
//                        "/api/core/sync/reassignarGrupsAlumnes",
//                        "/api/core/sync/reassignarGrupsCorreuGSuiteToDatabase").hasAnyAuthority(RolDto.ADMINISTRADOR.name(),RolDto.DIRECTOR.name(),RolDto.CAP_ESTUDIS.name())
//                .pathMatchers("/api/core/sync/sincronitza").hasAnyAuthority(RolDto.ADMINISTRADOR.name())
//                .pathMatchers("/api/core/sync/uploadfile").hasAnyAuthority(RolDto.ADMINISTRADOR.name(),RolDto.DIRECTOR.name())
//
//                //Usuaris
//                .pathMatchers("/api/core/usuaris/llistat/actius").hasAnyAuthority(RolDto.ADMINISTRADOR.name(),RolDto.DIRECTOR.name(),RolDto.CAP_ESTUDIS.name(),RolDto.ADMINISTRATIU.name())
//                .pathMatchers("/api/core/usuaris/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(),RolDto.DIRECTOR.name(),RolDto.CAP_ESTUDIS.name())
//
//                //Curs
//                .pathMatchers("/api/core/curs/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(),RolDto.DIRECTOR.name(),RolDto.CAP_ESTUDIS.name())
//
//                //Grups
//                .pathMatchers("/api/core/grup/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(),RolDto.DIRECTOR.name(),RolDto.CAP_ESTUDIS.name())
//
//                //Departament
//                .pathMatchers("/api/core/departament/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(),RolDto.DIRECTOR.name(),RolDto.CAP_ESTUDIS.name())
//
//                //Llistats
//                .pathMatchers("/api/core/google/sheets/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(),RolDto.DIRECTOR.name(),RolDto.CAP_ESTUDIS.name())
//
//                //Mòdul convalidacions
//                .pathMatchers("/api/convalidacions/**").hasAnyAuthority(RolDto.ADMINISTRADOR.name(),RolDto.DIRECTOR.name(),RolDto.CAP_ESTUDIS.name(),RolDto.ADMINISTRATIU.name())
//
//                .anyExchange().authenticated()
//                .and()
//                .csrf().disable()
//                .httpBasic().disable()
//                .formLogin().disable()
//                .cors().configurationSource(corsConfigurationSource())
//                .and()
//                .build();
//    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(this.allowedCors.split(",")));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type","Authorization"));
        configuration.setMaxAge(3600L);
        configuration.setAllowCredentials(true);
        configuration.applyPermitDefaultValues();

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
