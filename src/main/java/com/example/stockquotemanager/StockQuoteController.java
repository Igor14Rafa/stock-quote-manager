package com.example.stockquotemanager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

@Controller
@RequestMapping(path="/")
public class StockQuoteController {
    @Autowired
    private StockQuoteRepository stockQuoteRepository;
    private List<String> stocks = new ArrayList<String>();
    private final RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public void init(){
        String url = "http://stock-manager:8080/notification";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("host", "stock-quote-manager");
        map.add("port", "8081");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity( url, request , String.class );
    }

    @PostMapping(path="/add")
    public @ResponseBody String addNewQuote (@RequestParam String id
            , @RequestParam Date date
            , @RequestParam Integer price) {

        if(stocks.isEmpty()){
            String url = "http://stock-manager:8080/stock";
            String resp = restTemplate.getForObject(url, String.class);

            JsonParser springParser = JsonParserFactory.getJsonParser();
            List<Object> list = springParser.parseList(resp);

            for(Object o : list) {
                if (o instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) o;
                    int i = 0;
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if(entry.getKey().toString() == "id"){
                            stocks.add(entry.getValue().toString());
                        }
                        i++;
                    }
                }
            }
        }

        if(!stocks.contains(id)){
            return "Stock not registered yet!";
        }

        StockQuote n = new StockQuote();
        n.setIdStock(id);
        n.setDate(date);
        n.setPrice(price);
        stockQuoteRepository.save(n);
        return "Saved " + n.getIdStock().toString();
    }

    @GetMapping(path="/stockquotes")
    public @ResponseBody String getAllStockQuotes() {
        ObjectMapper obj = new ObjectMapper();
        String result = "";

        try{
            for(int i=0;i < stocks.size();i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", stocks.get(i));
                Map<Date, Integer> quotes = new HashMap<>();
                for (StockQuote stq: stockQuoteRepository.findAll()){
                    if(stq.getIdStock().equals(stocks.get(i))){
                        quotes.put(stq.getDate(), stq.getPrice());
                    }
                }
                map.put("quotes", quotes);
                result += obj.writeValueAsString(map);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }

    @GetMapping(path="/stockquote")
    public @ResponseBody StockQuote getQuote (@RequestParam Integer id) {
        Optional<StockQuote> n = stockQuoteRepository.findById(id);
        return n.get();
    }

    @GetMapping(path="/stocks")
    public @ResponseBody Iterator<String> getAllStocks() {
        return stocks.iterator();
    }

    @DeleteMapping(path="/stockcache")
    public @ResponseBody String clearStockCache(){
        stocks = new ArrayList<>();
        return "Cache cleared";
    }
}

