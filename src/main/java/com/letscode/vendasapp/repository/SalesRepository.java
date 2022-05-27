package com.letscode.vendasapp.repository;

import com.letscode.vendasapp.domain.Sale;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends ReactiveMongoRepository<Sale, String> {
}
