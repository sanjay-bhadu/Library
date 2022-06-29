package com.example.userDefined.Repo;

import com.example.userDefined.Model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepo extends JpaRepository<Issue,Integer> {

}
