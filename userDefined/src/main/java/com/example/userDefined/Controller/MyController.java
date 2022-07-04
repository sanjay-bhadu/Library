package com.example.userDefined.Controller;

import com.example.userDefined.Model.Book;
import com.example.userDefined.Model.Issue;
import com.example.userDefined.Model.Student;
import com.example.userDefined.Repo.BookRepo;
import com.example.userDefined.Repo.IssueRepo;
import com.example.userDefined.Repo.StudentRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log4j2
public class MyController {

    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private IssueRepo issueRepo;

    @GetMapping("/inventory")
    public String inventory()
    {
        List<Book> books=getAllBooks();
        List<Book> books1= books.stream()
                .filter(s -> s.isAvailable()).toList();
        int issue=books.size()-books1.size();
        return books.toString()+"\n"+"Available Books :"+books1.size()+"\n"+"Issued Book:"+issue;
    }
    @GetMapping("/books")
    public List<Book> getAllBooks()
    {
        log.info("The Books are accessed");
        return bookRepo.findAll();
    }

    @GetMapping("/book")
    public List<Book> getAvailableBook(){
        return bookRepo.findAll().stream().filter(s->s.isAvailable()).collect(Collectors.toList());
    }


    @PostMapping("/book")
    public Book addBook(@RequestBody Book book)
    {
        try{
            bookRepo.save(book);
            log.info("The Book has been Added to the Library "+book);
            return book;
        }
        catch (Exception e)
        {
            log.error(e);
            return null;
        }
    }

    @GetMapping("/book/{id}")
    public Book getABook(@PathVariable String id)
    {
        try{
            Book temp=bookRepo.getById(Integer.parseInt(id));
            if(temp==null)
                throw new RuntimeException("The Book you asked is not present in the Database");
            else {
                log.info("The Book has been accessed with id: "+id);
                return temp;
            }
        }
        catch (Exception e)
        {
            log.error(e);
            return null;
        }
    }

    @DeleteMapping("/book/{id}")
    public String deleteBook(@PathVariable String id)
    {
        try{
            Book temp=bookRepo.getById(Integer.parseInt(id));
            if(temp!=null)
            {
                log.info("The Book : "+temp+" has been Deleted from the Database");
                bookRepo.delete(temp);
                return "The Book has been deleted Successfully";
            }
            else{
                throw new RuntimeException("The Book is not present in the Database");
            }
        }
        catch (Exception e)
        {
            log.error(e);
            return e.getLocalizedMessage();
        }
    }

    @PutMapping("/book/{id}")
    public Book updateBook(@PathVariable String id,@RequestBody Book book)
    {
        try{
            Book temp=bookRepo.getById(Integer.parseInt(id));
            if(temp!=null){
                temp.setAvailable(book.isAvailable());
                temp.setName(book.getName());
                temp.setAuthor(book.getAuthor());
                bookRepo.save(temp);
                log.info("The Book with id: "+id+" has been updated to :"+temp);
                return temp;
            }
            else {
                throw new RuntimeException("The Book with Id : "+id+" is not present");
            }
        }
        catch (Exception e)
        {
            log.error(e);
            return null;
        }
    }

    @GetMapping("/student")
    public List<Student> getStudent()
    {
        log.info("The All list of Student has been Accessed");
        return studentRepo.findAll();
    }

    @GetMapping("/student/{id}")
    public Student getStudentById(@PathVariable String id)
    {
        try{
            Student temp=studentRepo.getById(Integer.parseInt(id));
            if(temp!=null)
            {
                log.info("The student has been accessed with id "+id+" and details "+temp);
                return temp;
            }
            else
                throw new RuntimeException("The student is not present in the Database server");
        }
        catch (Exception e)
        {
            log.error(e);
            return null;
        }
    }

    @PostMapping("/student")
    public Student addStudent(@RequestBody Student student)
    {
        try {
            studentRepo.save(student);
            log.info("The new student has been added to the database... Student : "+student);
            return student;
        }
        catch (Exception e)
        {
            log.error(e);
            return null;
        }
    }

    @PutMapping("/student/{id}")
    public Student updateStudent(@PathVariable String id,@RequestBody Student student)
    {
        try{
            Student temp=studentRepo.getById(Integer.parseInt(id));
            if(temp!=null)
            {
                temp.setName(student.getName());
                temp.setEmail(student.getEmail());
                studentRepo.save(temp);
                log.info("The Student with id "+id+" has been updated: "+temp);
                return temp;
            }
            else {
                throw new RuntimeException("The student with id "+id+" is not present in Database");
            }
        }
        catch (Exception e)
        {
            log.error(e);
            return null;
        }
    }

    @GetMapping("/issue")
    public List<Issue> getAllIssue()
    {
        try{
            log.info("The Issue list has been accessed");
            return issueRepo.findAll();
        }
        catch (Exception e)
        {
            log.error(e);
            return null;
        }
    }

    @GetMapping("/issue/{id}")
    public Issue getIssue(@PathVariable String id)
    {
        try{
            Issue temp=issueRepo.getById(Integer.parseInt(id));
            if(temp!=null)
            {
                log.info("The issue with id : "+id+" has been accessed "+temp);
                return temp;
            }
            else
                throw new RuntimeException("The issue has not been found in the Database");
        }
        catch (Exception e)
        {
            log.error(e);
            return null;
        }
    }

    @PostMapping("/issue")
    public Issue issueBook(@RequestParam int book_id,@RequestParam int student_id)
    {
        try{
            Student student=studentRepo.getById(student_id);
            Book book=bookRepo.getById(book_id);
            if(book!=null && student!=null)
            {
                book.setAvailable(false);
                book=updateBook(String.valueOf(book_id),book);
                Issue issue=new Issue();
                issue.setBook(book);
                issue.setStudent(student);
                issueRepo.save(issue);
                log.info("The Book "+book+" has been issued to "+student);
                return issue;
            }
            else if(book==null)
                throw new RuntimeException("Book with id "+book_id+" is not present in the Database");
            else if(student==null)
                throw new RuntimeException("Student with id "+student_id+" is not present in the Database");
            else {
                throw new RuntimeException("The Book with id "+book_id+" and customer with id "+student_id+" is not present in Database");
            }
        }
         catch (Exception e)
         {
             log.error(e);
             return null;
         }
    }

    @DeleteMapping("/return/{id}")
    public String returnBook(@PathVariable String id)
    {
        try{
            Issue issue=issueRepo.getById(Integer.parseInt(id));
            if(issue!=null)
            {
                Book book= issue.getBook();
                book.setAvailable(true);
                book=updateBook(String.valueOf(book.getId()),book);
                log.info("The Book has been returned "+book);
                issueRepo.delete(issue);
                return "The Book is successfully Returned";
            }
            else{
                throw new RuntimeException("Tt is not been issued");
            }
        }
        catch (Exception e)
        {
            log.error(e);
            return e.getLocalizedMessage();
        }
    }

    @GetMapping("/issueStudent/{id}")
    public List<Book> getBookByStudent(@PathVariable int id)
    {
        try{
           return issueRepo.findByStudentId(id);
        }
        catch (Exception e)
        {
            log.error(e);
            return null;
        }
    }

    @GetMapping("/issueBook/{id}")
    public List<Student> getStudentByBook(@PathVariable int id)
    {
        try{
            return issueRepo.findByBookId(id);
        }
        catch (Exception e)
        {
            log.error(e);
            return null;
        }
    }

}
