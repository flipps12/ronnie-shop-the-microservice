package com.ronnie.orders_service.services;

import com.ronnie.orders_service.config.WebClientConfig;
import com.ronnie.orders_service.models.dtos.*;
import com.ronnie.orders_service.models.entities.Orders;
import com.ronnie.orders_service.models.entities.OrdersItems;
import com.ronnie.orders_service.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;


    public String placeOrder(OrderRequest orderRequest) {
        // Check stock and more
        System.out.println(orderRequest);
        System.out.println(orderRequest.getOrderItems());
        BaseResponse result =  this.webClientBuilder.build() // peticion checkear stock
                .post()
                .uri("http://localhost:8082/api/product/in-stock")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(orderRequest.getOrderItems())
                .retrieve()
                .bodyToMono(BaseResponse.class)
                .block();


        if (result != null && !result.hasError()) {


            Orders orders = new Orders(); // generar orden
            orders.setOrderNumber(UUID.randomUUID().toString());
            orders.setSeller(orderRequest.getOrderItems().getFirst().getSeller());
            orders.setPaid(false);
            orders.setEmail(orderRequest.getEmail());
            orders.setName(orderRequest.getName());
            orders.setSurname(orderRequest.getSurname());
            orders.setOrdersItemsList(orderRequest.getOrderItems().stream()
                    .map(orderItemRequest -> mapOrderItemRequestToOrderItem(orderItemRequest, orders))
                    .toList());

            List<MercadoPagoPreferenceItemsRequest> orderItems = new ArrayList<>();

            String resultInitPoint =  this.webClientBuilder.build() // peticion crear metodo de pago
                .post()
                .uri("http://localhost:8083/api/mercado-pago/create-preference")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(MercadoPagoPreferenceRequest.builder()
                        .externalReference(orders.getOrderNumber())
                        .email(orders.getEmail())
                        .name(orders.getName())
                        .surname(orders.getSurname())
                        .seller(orders.getSeller())
                        .items(orders.getOrdersItemsList().stream()
                                .map(this::mapOrderPreferencesItems)
                                .toList())
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
            System.out.println(resultInitPoint);
            orders.setInitPoint(resultInitPoint);
            orderRepository.save(orders);
            return resultInitPoint;
        } else {
            throw new IllegalArgumentException("Sin stock");
        }
    }

    private MercadoPagoPreferenceItemsRequest mapOrderPreferencesItems(OrdersItems ordersItemsPreference) {
        return MercadoPagoPreferenceItemsRequest.builder()
                .title("Codigo de orden: " + ordersItemsPreference.getSku())
                .price(getPrice(ordersItemsPreference.getSku()))
                .quantity(ordersItemsPreference.getQuantity())
                .description("")
                .build();
    }

    private OrdersItems mapOrderItemRequestToOrderItem(OrderItemRequest orderItemRequest, Orders orders) {
        return OrdersItems.builder()
                .id(orders.getId())
                .sku(orderItemRequest.getSku())
                .quantity(orderItemRequest.getQuantity())
                .order(orders)
                .build();
    }

    private Double getPrice(String sku) {
        return webClientBuilder.build() // peticion checkear stock
                .get()
                .uri("http://localhost:8082/api/product/" + sku)
                .retrieve()
                .bodyToMono(Double.class)
                .block();
    }
}
