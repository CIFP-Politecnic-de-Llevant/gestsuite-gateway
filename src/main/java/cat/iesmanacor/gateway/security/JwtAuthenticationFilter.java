package cat.iesmanacor.gateway.security;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    @Autowired
    private ReactiveAuthenticationManager authenticationManager;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(authHeader-> authHeader.startsWith("Bearer "))
                .switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
                .map(token -> token.replace("Bearer ",""))
                .flatMap(token -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(null,token)))
                .flatMap(authentication -> {
                    if(authentication==null){
                        return Mono.empty();
                    }
                    return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                });
    }
}
