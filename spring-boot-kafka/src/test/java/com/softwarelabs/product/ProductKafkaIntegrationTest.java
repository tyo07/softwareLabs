package com.softwarelabs.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.softwarelabs.config.BaseIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
public class ProductKafkaIntegrationTest extends BaseIntegrationTest {

	@Autowired
	ProductProducer productProducer;

	@Autowired
	ProductConsumer productConsumer;

	@Autowired
	ProductService productService;

	@Test
	public void updateProduct_ifProductChangeEventSent() throws JsonProcessingException, InterruptedException {
		//Save the product which price will be change
		String productName = "product1";
		Product newProduct = new PersistantProduct(productName);
		productService.saveProduct(newProduct);

		//Sent price change event
		productProducer.updateProductPrice();
		Thread.sleep(1000);

		//Product should be updated with new price
		productConsumer.runConsumer();
		Thread.sleep(1000);

		//Check product is updated
		Product updatedProductParam = new PersistantProduct(productName);
		Product updatedProduct = productService.getProduct(updatedProductParam);
		Assert.assertEquals(productName, updatedProduct.name());
		Assert.assertNotNull(updatedProduct.price());
		Assert.assertNotEquals(updatedProduct.price(), BigDecimal.ZERO);
	}

}
