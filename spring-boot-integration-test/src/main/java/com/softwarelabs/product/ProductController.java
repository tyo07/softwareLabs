package com.softwarelabs.product;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class ProductController implements IProductPort {

	private final ProductService productService;
	private final ProductMapper productMapper;

	public ProductController(ProductService productService, ProductMapper productMapper) {
		this.productService = productService;
		this.productMapper = productMapper;
	}

	@Override
	public ProductResponse createProduct(ProductRequest request) {
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
