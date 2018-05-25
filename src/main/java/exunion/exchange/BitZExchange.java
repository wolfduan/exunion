package exunion.exchange;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import exunion.metaobjects.Account;
import exunion.metaobjects.Depth;
import exunion.metaobjects.Depth.PriceQuotation;
import exunion.metaobjects.Order;
import exunion.metaobjects.OrderSide;
import exunion.metaobjects.Ticker;
import exunion.metaobjects.Account.Balance;
import exunion.standardize.Standardizable;
import exunion.util.UrlParameterBuilder;

public class BitZExchange extends AExchange {

	private static final String PLANTFORM = "bit-z.com";
	
	private static final String serverHost = "https://www.bit-z.pro";
	
	Logger logger = LogManager.getLogger(BitZExchange.class);
	
	Standardizable<String, String> currencyStandardizer = new Standardizable<String, String>() {

		@Override
		public String standardize(String l) {
			return l.toUpperCase();
		}

		@Override
		public String localize(String s) {
			return s.toLowerCase();
		}
	};
	
	Standardizable<String, String> orderSideStandizer = new Standardizable<String, String>() {

		@Override
		public String standardize(String l) {
			return "in".equals(l) ? OrderSide.BUY : "out".equals(l) ? OrderSide.SELL : "unknow";
		}

		@Override
		public String localize(String s) {
			return OrderSide.BUY.equals(s) ? "in" : OrderSide.SELL.equals(s) ? "out" : "unknow";
		}
		
	};
	
	public BitZExchange(String key, String secret, Boolean needProxy) {
		super(key, secret, needProxy);
	}

	@Override
	public Account getAccount() {
		Map<String, String> params = new HashMap<>();
		params.put("api_key", key);
		params.put("timestamp", new Long(System.currentTimeMillis()/1000).toString());
		params.put("nonce", nonce());
		String urlParams = UrlParameterBuilder.buildUrlParamsWithMD532Sign(secret,"sign", params);
		String json = client.get("https://www.bit-z.com/api_v1/balances?" + urlParams);
		if(null == json){
			logger.error("{}服务器无数据返回。", this.getPlantformName());
			return null;
		}
		
		if(!json.contains("Success")){
			logger.error("获取账户信息时{}服务器返回错误信息: {}。", PLANTFORM, json);
			return null;
		}
		Account account = new Account();
		
		JSONObject jsonObject = JSON.parseObject(json).getJSONObject("data");
		for(String k : jsonObject.keySet()){
			if(k.equals("uid") || k.contains("_")){
				continue;
			}
			Balance bal = new Balance();
			bal.setAsset(k.toUpperCase());
			bal.setFree(new BigDecimal(jsonObject.getString(k + "_over")));
			bal.setLocked(new BigDecimal(jsonObject.getString(k + "_lock")));
			account.putBalance(bal);
		}
		return account;
	}

	@Override
	public Depth getDepth(String currency) {
		Map<String, String> params = new HashMap<>();
		params.put("coin", currencyStandardizer.localize(currency));
		String urlParams = UrlParameterBuilder.MapToUrlParameter(params);
		String json = client.get("https://www.bit-z.com/api_v1/depth?" + urlParams);
		if(null == json ){
			logger.error("{}服务器无数据返回。", PLANTFORM);
			return null;
		}
		if(!json.contains("Success")){
			logger.error("{}服务器返回错误信息: {}", PLANTFORM, json);
			return null;
		}
		
		Depth depth = new Depth();
		
		JSONObject jsonObject = JSON.parseObject(json).getJSONObject("data");
		JSONArray asksArray = jsonObject.getJSONArray("asks");
		JSONArray bidsArray = jsonObject.getJSONArray("bids");
		
		List<PriceQuotation> asks = new ArrayList<>();
		for(int i=0; i<asksArray.size(); i++){
			JSONArray quotation = asksArray.getJSONArray(i);
			BigDecimal price = new BigDecimal(quotation.get(0).toString());
			BigDecimal quantity = new BigDecimal(quotation.get(1).toString());
			Depth.PriceQuotation priceQuotation = new PriceQuotation(price, quantity);
			asks.add(priceQuotation);
		}
		depth.setAsks(asks);
		
		List<PriceQuotation> bids = new ArrayList<>();
		for(int i=0; i<bidsArray.size(); i++){
			JSONArray quotation = bidsArray.getJSONArray(i);
			BigDecimal price = new BigDecimal(quotation.get(0).toString());
			BigDecimal quantity = new BigDecimal(quotation.get(1).toString());
			Depth.PriceQuotation priceQuotation = new PriceQuotation(price, quantity);
			bids.add(priceQuotation);
		}
		depth.setBids(bids);
		depth.setExchange(PLANTFORM);
		depth.setCurrency(currency);
		depth.setTimestamp(jsonObject.getLong("date") * 1000);
		return depth;
	}
	
	@Override
	public Ticker getTicker(String currency) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Ticker> getAllTickers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order getOrder(String currency, String orderId) {
		return null;
	}

	@Override
	public List<Order> getOpenOrders(String currency, String side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> getOpenOrders(String currency) {
		Map<String, String> params = new HashMap<>();
		params.put("api_key", key);
		params.put("coin", currencyStandardizer.localize(currency));
		params.put("stimestamp", new Long(System.currentTimeMillis()/1000).toString());
		params.put("nonce", nonce());
		String urlParams = UrlParameterBuilder.buildUrlParamsWithMD532Sign(secret, "sign", params);
		System.out.println(serverHost + "/api_v1/openOrders?" + urlParams);
		String json = client.post(serverHost + "/api_v1/openOrders?" + urlParams);
		if(null == json){
			logger.error("获取进行中订单currency={}时服务器{}无数据返回。", currency, PLANTFORM);
			return null;
		}
		
		if(!json.contains("Success")){
			logger.error("获取进行中订单currency={}时服务器{}返回错误信息: {}", currency, PLANTFORM, json);
			return null;
		}
		
		return null;
	}

	@Override
	public List<Order> getHistoryOrders(String currency) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order order(String side, String currency, BigDecimal quantity, BigDecimal price) {
		Map<String, String> params = new HashMap<>();
		params.put("api_key", key);
		params.put("tim&stimestamp", new Long(System.currentTimeMillis()/1000).toString());
		params.put("nonce", nonce());
		params.put("type", orderSideStandizer.localize(side));
		
		return null;
	}

	@Override
	public Order cancel(String currency, String orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPlantformName() {
		return PLANTFORM;
	}
	
	private String nonce(){
		return new Double(Math.random()).toString().substring(2, 8);
	}

}