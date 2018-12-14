package com.product.inventory.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.product.inventory.db.InventoryData;
import com.product.inventory.db.ItemQuality;
import com.product.inventory.model.ItemInput;
import com.product.inventory.model.Result;

@Service
public class InventoryService {
	/**
	 * Add new product to the repository
	 * 
	 * @param item
	 */
	public void addProduct(ItemInput item) {
		item.setEntryDate(getDate());
		Date sellInDate = getDate();
		sellInDate.setDate(sellInDate.getDate() + item.getSellIn());
		item.setSellDate(sellInDate);
		InventoryData.getProductsList().add(item);
	}

	/**
	 * Get the list of products registered currently
	 * 
	 * @param reportDate
	 * @return List<ResultItem>
	 */
	public List<Result> getProducts(String reportDate) {
		Date inputDate = getDate(reportDate);
		List<Result> result = new ArrayList();
		for (ItemInput item : InventoryData.getProductsList()) {
			result.add(getProcessedItem(item, inputDate));
		}
		return result;
	}

	/**
	 * Calculate the quality for each item 1. get the date based decrement and
	 * sellIn special rules as QualityDetails 2. create result object set sellIn as
	 * per the rules like sellDate crossed or Items like sufuras which is never sold
	 * calculate the quality and set in result object 3. quality of an item cannot
	 * be negetive or more than 50
	 * 
	 * @param item
	 * @param reportDate
	 * @return
	 */
	public Result getProcessedItem(ItemInput item, Date reportDate) {
		Result item1 = new Result();
		item1.setName(item.getName());

		ItemQuality qualityDetails = getQualityDetails(item.getName());

		setSellIn(item1, qualityDetails.isNeverSold(), getDateDiff(item.getSellDate(), reportDate));
		setQuality(item1, qualityDetails, reportDate, item);

		checkQualityRules(item1);

		return item1;
	}

	/**
	 * Set sellIn date based on item never sold or sold and sellIn date is in future
	 * 
	 * @param item
	 * @param neverSold
	 * @param sellDiff
	 */
	public void setSellIn(Result item, boolean neverSold, int sellDiff) {
		if (neverSold) {
			item.setSellIn(0);
		} else {
			item.setSellIn(sellDiff > 0 ? sellDiff : 0);
		}
	}

	/**
	 * Calculate the quality basedon the date requested
	 * 
	 * @param item1
	 * @param qualityDetails
	 * @param reportDate
	 * @param item
	 */
	private void setQuality(Result item1, ItemQuality qualityDetails, Date reportDate, ItemInput item) {

		int sellInToReportDiff = getDateDiff(item.getSellDate(), reportDate);
		int entryToReportDateDiff = getDateDiff(reportDate, item.getEntryDate());
		int daysAfterSellDate = getDateDiff(reportDate, item.getSellDate());
		int entryDateToSellDate = getDateDiff(item.getSellDate(), item.getEntryDate());

		if (entryDateToSellDate > 10) {
			if (daysAfterSellDate <= 0) {
				if (sellInToReportDiff > 10) {
					item1.setQuality(item.getQuality() - (entryToReportDateDiff * qualityDetails.getDecrement()));
				} else {
					int first = entryDateToSellDate - 10;
					item1.setQuality(item.getQuality() - (first * qualityDetails.getDecrement())
							- ((entryDateToSellDate - sellInToReportDiff - first)
									* qualityDetails.getDecrementBefore10()));
				}
			} else {
				if (qualityDetails.isZeroAfterSellDate()) {
					item1.setQuality(0);
				} else {
					int firstDecrement = (entryDateToSellDate - 10) * qualityDetails.getDecrement();
					int tenDaysBefore = 10 * qualityDetails.getDecrementBefore10();
					int afterDecrement = daysAfterSellDate * qualityDetails.getDecrementAfterSellDate();
					item1.setQuality(item.getQuality() - firstDecrement - tenDaysBefore - afterDecrement);
				}
			}
		} else {
			if (daysAfterSellDate <= 0) {
				item1.setQuality(item.getQuality() - (entryToReportDateDiff * qualityDetails.getDecrementBefore10()));
			} else {
				if (qualityDetails.isZeroAfterSellDate()) {
					item1.setQuality(0);
				} else {
					item1.setQuality(item.getQuality() - (entryDateToSellDate * qualityDetails.getDecrementBefore10())
							- (daysAfterSellDate * qualityDetails.getDecrementAfterSellDate()));
				}
			}
		}
	}

	/**
	 * Check the quality valuenot negetive or not more than 50
	 * 
	 * @param item1
	 */
	private void checkQualityRules(Result item1) {
		if (item1.getQuality() < 0)
			item1.setQuality(0);
		if (item1.getQuality() > 50)
			item1.setQuality(50);
	}

	public static Date getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date dateWithoutTime = null;
		try {
			dateWithoutTime = sdf.parse(sdf.format(new Date()));
		} catch (ParseException e) {
			System.out.println("exception " + e);
		}
		return dateWithoutTime;
	}

	public static Date getDate(String date) {
		try {
			return new SimpleDateFormat("dd-MM-yyyy").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	public static int getDateDiff(Date date1, Date date2) {
		long diffInMillies = date1.getTime() - date2.getTime();
		int diff = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		return diff;
	}

	public static ItemQuality getQualityDetails(String name) {
		ItemQuality qualityDetails = InventoryData.getProductQualityMap().get(name);
		if (qualityDetails == null) {
			qualityDetails = InventoryData.getProductQualityMap().get("Default");
		}
		return qualityDetails;
	}

}
