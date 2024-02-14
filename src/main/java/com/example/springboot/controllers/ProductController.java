package com.example.springboot.controllers;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.services.ProductService;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import jakarta.validation.Valid;

@RestController
public class ProductController {
    
    @Autowired
    private ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto) {
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDto, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(productModel));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts() {
        List<ProductModel> productsList = productService.findAllProducts();
        if (!productsList.isEmpty()) {
            for(ProductModel product : productsList) {
                UUID id = product.getIdProduct();
                product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(productsList);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getOneProduct(@PathVariable(value="id") UUID id) {
        ProductModel product = productService.findById(id);
        if (product.equals(null)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }
        product.add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Products List"));
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value="id") UUID id, @RequestBody @Valid ProductRecordDto productRecordDto) {
        ProductModel product = productService.findById(id);
        if(product.equals(null)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }
        BeanUtils.copyProperties(productRecordDto, product);
        return ResponseEntity.status(HttpStatus.OK).body(productService.update(product));
    }

    @DeleteMapping("products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value="id") UUID id) {
        ProductModel product = productService.findById(id);
        if(product.equals(null)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }
        productService.delete(product.getIdProduct());
        return ResponseEntity.status(HttpStatus.OK).body("Product Deleted Successfully");
    }
}
