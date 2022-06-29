package com.example.userDefined.Repo;

import com.example.userDefined.Model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepo extends JpaRepository<Book,Integer> {
@Query("select b from Book b where b.id = ?1")
Book getById(int id);
}
