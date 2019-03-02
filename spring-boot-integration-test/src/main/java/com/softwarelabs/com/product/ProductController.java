package com.softwarelabs.com.product;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@Slf4j
public class ProductController implements com.softwarelabs.com.product.IProductPort {

  private final ProductService productService;
  private final ProductMapper productMapper;

  public ProductController(com.softwarelabs.com.product.ProductService productService, com.softwarelabs.com.product.ProductMapper productMapper) {
    this.productService = productService;
    this.productMapper = productMapper;
  }

  @RequestMapping("/")
  public @ResponseBody String greeting() {
    return "Hello World";
  }

  @Override
  public IProductPort.ProductResponse createProduct(ProductRequest request) {
    Product product = productService.createProduct(request.getName(), request.getId());
    ProductDto productDto = productMapper.mapToProductDto(product);
    ProductResponse response = new ProductResponse();
    response.setProduct(productDto);
    response.setResult(new IProductPort.Result().setMessage("Success").setSuccess(true));
    return response;
  }

  @Override
  public ProductResponse getProductById(Long productId) {
    log.info(productId.toString());
    Product product = productService.getProduct(productId);
    ProductDto productDto = productMapper.mapToProductDto(product);
    IProductPort.ProductResponse response = new ProductResponse();
    response.setProduct(productDto);
    response.setResult(new IProductPort.Result().setMessage("Success").setSuccess(true));
    return response;
  }
}
