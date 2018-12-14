package com.product.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.product.inventory.model.ItemInput;
import com.product.inventory.model.Result;
import com.product.inventory.service.InventoryService;

@RestController("/")
public class InventoryRestController {
	// @Autowired
	InventoryService service = new InventoryService();

	/**
	 * Add new product with sellInDate and quality value
	 * 
	 * @param item
	 * @param model
	 * @return String
	 */
	@PostMapping("/product")
	public ResponseEntity<String> addProduct(@RequestBody ItemInput item, Model model) {
		service.addProduct(item);
		return new ResponseEntity<String>("{\"result\":\"New product added\"}", HttpStatus.OK);
	}

	/**
	 * Return the list of products with sellInDate and quality value for the given date
	 * @param reportDate
	 * @return List<ResultItem>
	 * @throws ParseException
	 */
	@GetMapping("/products/{date}")
	public ResponseEntity<List<Result>> getProducts(@PathVariable("date") String date)
			throws ParseException {
		List<Result> items = service.getProducts(date);
		return new ResponseEntity<List<Result>>(items, HttpStatus.OK);
	}

}