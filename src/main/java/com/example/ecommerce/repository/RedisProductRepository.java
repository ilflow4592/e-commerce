package com.example.ecommerce.repository;


import com.example.ecommerce.entity.RedisProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisProductRepository extends CrudRepository<RedisProduct, Long> {

}
