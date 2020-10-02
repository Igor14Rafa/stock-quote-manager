package com.example.stockquotemanager;

import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface StockQuoteRepository extends CrudRepository<StockQuote, Integer> {

}
