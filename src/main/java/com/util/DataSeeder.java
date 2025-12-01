package com.util;



import com.model.Product;
import com.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            productRepository.save(new Product("iPhone 12", "Apple iPhone 12"));
            productRepository.save(new Product("Samsung Galaxy S21", "Samsung S21 smartphone"));
            productRepository.save(new Product("Google Pixel 6", "Google Pixel 6"));
            productRepository.save(new Product("OnePlus Nord", "OnePlus midrange device"));
        }
    }
}
