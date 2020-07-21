package br.com.codenation.service;

import java.util.*;
import java.util.stream.Collectors;

import br.com.codenation.model.OrderItem;
import br.com.codenation.model.Product;
import br.com.codenation.repository.ProductRepository;
import br.com.codenation.repository.ProductRepositoryImpl;

public class OrderServiceImpl implements OrderService {

	private final ProductRepository productRepository = new ProductRepositoryImpl();
	private final double discountPercent = 0.2d;


	/**
	 * Calculate the sum of all OrderItems
	 * Esse método deverá receber uma lista de OrderItem (Classe que contem o id do produto e sua quantidade
	 * 	no pedido) e deve retornar o valor total do pedido. Para calcular o valor total, deve-se obter o valor de cada
	 * 	item do pedido (OrderItem) multiplicando a quantidade de itens pelo valor do produto e, caso o produto tenha o
	 * 	atributo isSale igual a true, deve-se aplicar um desconto de 20%.
	 */
	@Override
	public Double calculateOrderValue(List<OrderItem> items) {
		return items.stream()
				.mapToDouble(item -> {
					Optional<Product> product = productRepository.findById(item.getProductId());
			return discountValue(product, item);
				}).sum();
	}

	/**
	 * Map from idProduct List to Product Set
	 */
	@Override
	public Set<Product> findProductsById(List<Long> ids) {
		return ids.stream()
				.map(productRepository::findById)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toSet());
	}

	/**
	 * Calculate the sum of all Orders(List<OrderIten>)
	 * Esse método deverá calcular o valor total de todos os pedidos, sendo que cada pedido corresponde a uma lista
	 * de OrderItem. Para calcular o valor total de cada pedido, você deve seguir as mesmas regras do método
	 * calculateOrderValue.
	 */
	@Override
	public Double calculateMultipleOrders(List<List<OrderItem>> orders) {
		return orders.stream()
				.mapToDouble(this::calculateOrderValue).sum();
	}

	/* Group products using isSale attribute as the map key */
	@Override
	public Map<Boolean, List<Product>> groupProductsBySale(List<Long> productIds) {
		/*Exemplo: https://www.baeldung.com/java-groupingby-collector
		* Map<BlogPostType, List<BlogPost>> postsPerType = posts.stream().collect(groupingBy(BlogPost::getType));
		*/
		return findProductsById(productIds).stream().collect(Collectors.groupingBy(Product::getIsSale));
	}

	public Boolean verifyDiscount(Optional<Product> product) {
		return product.isPresent() && product.get().getIsSale();
	}

	public Double discountValue (Optional<Product> product, OrderItem item){

		return verifyDiscount(product) ?
				product.get().getValue() - (product.get().getValue() * discountPercent) * item.getQuantity() :
				product.get().getValue() * item.getQuantity();
	}
}