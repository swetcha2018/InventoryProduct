package com.product.inventory.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.product.inventory.model.ItemInput;

public class InventoryData {
	private static List<ItemInput> productsList = new ArrayList<ItemInput>();

	private static Map<String, ItemQuality> productQualityMap = new HashMap();

	public static List<ItemInput> getProductsList() {
		return productsList;
	}

	public static Map<String, ItemQuality> getProductQualityMap() {
		return productQualityMap;
	}

	static {
		productQualityMap.put("Aged Brie", new ItemQuality("Aged Brie", -1, -1, -1, false, false));
		productQualityMap.put("Sulfuras", new ItemQuality("Sulfuras", 0, 0, 0, false, true));
		productQualityMap.put("Concert backstage passes",
				new ItemQuality("Concert backstage passes", -1, -2, 0, true, false));
		productQualityMap.put("Default", new ItemQuality("Default", 1, 1, 2, false, false));
	}
}
