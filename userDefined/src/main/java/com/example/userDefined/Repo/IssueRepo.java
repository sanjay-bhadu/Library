package com.example.userDefined.Repo;

import com.example.userDefined.Model.Book;
import com.example.userDefined.Model.Issue;
import com.example.userDefined.Model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepo extends JpaRepository<Issue,Integer> {

    @Query("select i from Issue i where i.id = ?1")
    Issue getById(int id);

    @Query(value = "select i.book from Issue i where i.student.id = :student_id")
    List<Book> findByStudentId(@Param("student_id") int student_id);

    @Query(value = "select i.student from Issue i where i.book.id = :book_id")
    List<Student> findByBookId(@Param("book_id") int book_id);
}
