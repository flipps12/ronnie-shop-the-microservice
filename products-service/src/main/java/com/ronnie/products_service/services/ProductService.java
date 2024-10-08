package com.ronnie.products_service.services;

import com.ronnie.products_service.entities.dtos.ProductRequest;
import com.ronnie.products_service.entities.dtos.ProductResponse;
import com.ronnie.products_service.entities.models.Product;
import com.ronnie.products_service.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void addProduct(ProductRequest productRequest, String username) {
        System.out.println(username);
        var product = Product.builder()
                .sku(productRequest.getSku())
                .seller(username)
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .limitPerOrder(productRequest.getLimitPerOrder())
                .StockNecessary(productRequest.getStockNecessary())
                .status(false)
                .pictureUrl(productRequest.getPictureUrl())
                .build();

        productRepository.save(product);
        log.info("Product added: {}", product);
    }

    public List<ProductResponse> getAllProducts() {
        var products = productRepository.findAll();
        return products.stream().map(this::mapToProductResponse).toList();
    }

    public List<ProductResponse> getProducts(String name) {
        var products = productRepository.findBySeller(name);
        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .seller(product.getSeller())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .limitPerOrder(product.getLimitPerOrder())
                .StockNecessary(product.getStockNecessary())
                .status(product.getStatus())
                .pictureUrl(product.getPictureUrl())
                .build();
    }

    public String deleteProduct(String username, Long id) {
        var product = productRepository.findById(id);
        if (product.isEmpty()) return "producto no existe";
        else if (!product.get().getSeller().equals(username)) return "sin permiso"; // agregar que un admin lo elimine igual
        productRepository.deleteById(id);
        return "Eliminado";
    }

    public void updateProduct(Product product, String username) {
        if (productRepository.findById(product.getId()).isEmpty()) throw new IllegalArgumentException("Id inexistente");
        var productDB = productRepository.findById(product.getId());
        if (!productDB.get().getSeller().equals(username) || !product.getSeller().equals(username)) throw new IllegalArgumentException("Vendedor incorrecto");
        productRepository.save(product);
    }

    // agregar editar
}
