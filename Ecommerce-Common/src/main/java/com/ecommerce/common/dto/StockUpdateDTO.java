package com.ecommerce.common.dto;

public class StockUpdateDTO {
	
	private Long productId;
	
    private Integer stockQuantity;
    
    public StockUpdateDTO() {
    }
    
	public StockUpdateDTO(Long productId, Integer stockQuantity) {
		super();
		this.productId = productId;
		this.stockQuantity = stockQuantity;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Integer getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(Integer stockQuantity) {
		this.stockQuantity = stockQuantity;
	}

}

