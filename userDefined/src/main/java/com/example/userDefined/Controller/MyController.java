package com.example.userDefined.Controller;

import com.example.userDefined.Model.Book;
import com.example.userDefined.Model.Issue;
import com.example.userDefined.Model.Student;
import com.example.userDefined.Repo.BookRepo;
import com.example.userDefined.Repo.IssueRepo;
import com.example.userDefined.Repo.StudentRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
public class MyController {

    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private IssueRepo issueRepo;

    @GetMapping(value = "/inventory",produces = MediaType.APPLICATION_JSON_VALUE)
    public String inventory()
    {
        try {
            List<Book> books = getAllBooks();
            int available = 0;
            int issue = 0;
            for (Book m : books) {
                available = available + m.getAvailable();
                issue = issue + m.getIssued();
            }
            return books+"\n"+"Available Books: "+available+"\n"+"Issued Books: "+issue;
        }
        catch (Exception e)
        {
            log.error(e);
            return e.getLocalizedMessage();
        }
    }
    @GetMapping("/book")
    public List<Book> getAllBooks()
    {
        log.info("The Books are accessed");
        return bookRepo.findAll();
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
                temp.setAuthor(book.getAuthor());
                temp.setName(book.getName());
                temp.setTotal(book.getTotal());
                temp.setAvailable(book.getAvailable());
                temp.setIssued(book.getIssued());
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
            if(book!=null && student!=null && book.getAvailable()>=1)
            {
                book.setAvailable(book.getAvailable()-1);
                book.setIssued(book.getIssued()+1);
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
                throw new RuntimeException("The Book with id "+book_id+" and customer with id "+student_id+" is not available for the issue");
            }
        }
         catch (Exception e)
         {
             log.error(e);
             return null;
         }
    }

    @DeleteMapping("/return/{book_id}")
    public String returnBook(@PathVariable String book_id,@RequestParam String student_id)
    {
        try{
            List<Issue> issue=issueRepo.findIssueByBookId(Integer.parseInt(book_id));
            if(!issue.isEmpty())
            {
                Issue temp=null;
                for(Issue i: issue)
                {
                    if(i.getStudent().getId()==Integer.parseInt(student_id))
                    {
                        temp=i;
                        break;
                    }
                }
                if(temp!=null)
                {
                    Book book=temp.getBook();
                    book.setAvailable(book.getAvailable()+1);
                    book.setIssued(book.getIssued()-1);
                    book=updateBook(String.valueOf(book.getId()),book);
                    log.info("The Book has been returned "+book);
                    issueRepo.delete(temp);
                    return "The Book is successfully Returned";
                }
                else {
                    throw new RuntimeException("The Book with "+book_id+" is not issued to "+student_id);
                }
            }
            else{
                throw new RuntimeException("It is not been issued");
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
