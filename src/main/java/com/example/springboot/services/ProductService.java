package com.example.springboot.services;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import com.example.springboot.services.exceptions.ObjectNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public ProductModel create(ProductModel productModel) {
        productModel.setIdProduct(null);
        return productRepository.save(productModel);
    }

    public ProductModel findById(UUID id) {
        Optional<ProductModel> product = productRepository.findById(id);
        return product.orElseThrow(() -> new ObjectNotFoundException("Not Found"));
    }

    public List<ProductModel> findAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public ProductModel update(ProductModel productModel) {
        ProductModel newProduct = findById(productModel.getIdProduct());
        return productRepository.save(newProduct);
    }

    @Transactional
    public void delete(UUID id) {
        findById(id);
        try {
            productRepository.deleteById(id);
        } catch(Exception e) {
            throw new RuntimeException("Can't Delete");
        }
    }


}
