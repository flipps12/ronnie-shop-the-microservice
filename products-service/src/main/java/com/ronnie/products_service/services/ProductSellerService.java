package com.ronnie.products_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
public class ProductSellerService {

    private final WebClient.Builder webClientBuilder; // plantear cambiar a Authservice

    public Boolean authCookie(String cookie) {
        String result =  this.webClientBuilder.build() // pedir role de la cookie
                .get()
                .uri("http://localhost:8080/api/auth/extract/" + cookie)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (result == null) return false;
        if (result.equals("seller") || result.equals("admin")) return true; // cambiar solo admin o admin y sellers
        return false;
    }
}