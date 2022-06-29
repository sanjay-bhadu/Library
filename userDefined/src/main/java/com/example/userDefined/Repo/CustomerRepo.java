package com.example.userDefined.Repo;

import com.example.userDefined.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepo extends JpaRepository<Customer,Integer> {

    @Query("select c from Customer c where c.id = ?1")
    Customer getById(int id);

}
