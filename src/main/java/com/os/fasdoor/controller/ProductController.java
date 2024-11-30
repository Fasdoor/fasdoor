//package com.os.fasdoor.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.os.fasdoor.service.ProductService;
//import org.json.JSONException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/product")
//public class ProductController {
//    private final ProductService productService;
//
//    @Autowired
//    public ProductController(ProductService productService) {
//        this.productService = productService;
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/add-product")
//    public ResponseEntity addProduct(@RequestBody String object) throws JsonProcessingException {
//        Map<String, Object> result = new HashMap<>();
//        productService.addProduct(object, result);
//        return result.containsKey("success") ? new ResponseEntity<>(result, HttpStatus.OK) : new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/delete")
//    public ResponseEntity deleteProduct(@RequestBody String object) throws JSONException {
//        Map<String, Object> result = new HashMap<>();
//        productService.deleteProduct(result, object);
//        return result.containsKey("success") ? new ResponseEntity<>(result, HttpStatus.OK) : new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
//    }
//}
