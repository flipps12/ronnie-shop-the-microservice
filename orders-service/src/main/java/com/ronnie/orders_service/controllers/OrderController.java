package com.ronnie.orders_service.controllers;

import com.ronnie.orders_service.models.dtos.OrderRequest;
import com.ronnie.orders_service.models.entities.Orders;
import com.ronnie.orders_service.repositories.OrderRepository;
import com.ronnie.orders_service.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @GetMapping // ver todos las ordenes
    @ResponseStatus(HttpStatus.OK)
    public List<Orders> getOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public List<Orders> getOrdersBySeller(@PathVariable("name") String name) {
        return orderService.getOrdersBySeller(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder(orderRequest);
    }

    @PostMapping(path = "/setStatus")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String processPayment(@RequestBody Map<String, Object> payment) {
        return orderService.resetStatus((String) payment.get("external_reference"));
    }

    // devoted edit
}
