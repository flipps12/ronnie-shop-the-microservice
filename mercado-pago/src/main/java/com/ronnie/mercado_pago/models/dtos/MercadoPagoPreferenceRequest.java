package com.ronnie.mercado_pago.models.dtos;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MercadoPagoPreferenceRequest {
    private String externalReference;
    // items
    private List<MercadoPagoPreferenceItemsRequest> items;
}
