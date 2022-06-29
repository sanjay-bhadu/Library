package com.example.userDefined.Repo;

import com.example.userDefined.Model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepo extends JpaRepository<Issue,Integer> {

    @Query("select i from Issue i where i.id = ?1")
    Issue getById(int id);

}
