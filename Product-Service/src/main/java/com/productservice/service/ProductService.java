package com.productservice.service;

import java.util.List;
import java.util.Optional;

import com.ecommerce.common.dto.ProductDTO;
import com.ecommerce.common.dto.StockUpdateDTO;

public interface ProductService {
	
	public String addProduct(ProductDTO productDTO);
	
	public String updateProduct(Long productId, ProductDTO productDTO);
	
	public String deleteProduct(Long productId);
	
	public List<ProductDTO> getAllProducts();
	
	public Optional<ProductDTO> getProductById(Long productId);
	
	public List<ProductDTO> searchProducts(String keyword);
	
	public List<ProductDTO> filterByCategory(String category);
	
	public List<ProductDTO> filterByPriceRange(Double minPrice, Double maxPrice);
	
	public void updateStock(StockUpdateDTO stockUpdateDTO);

}