package com.example.userDefined.Controller;

import com.example.userDefined.Model.Book;
import com.example.userDefined.Model.Customer;
import com.example.userDefined.Model.Issue;
import com.example.userDefined.Repo.BookRepo;
import com.example.userDefined.Repo.CustomerRepo;
import com.example.userDefined.Repo.IssueRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log4j2
public class MyController {

    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private IssueRepo issueRepo;
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
    @GetMapping("/customer")
    public List<Customer> getCustomer()
    {
        log.info("The All list of Customer has been Accessed");
        return customerRepo.findAll();
    }

    @GetMapping("/customer/{id}")
    public Customer getCustomerById(@PathVariable String id)
    {
        try{
            Customer temp=customerRepo.getById(Integer.parseInt(id));
            if(temp!=null)
            {
                log.info("The customer has been accessed with id "+id+" and details "+temp);
                return temp;
            }
            else
                throw new RuntimeException("The customer is not present in the Database server");
        }
        catch (Exception e)
        {
            log.error(e);
            return null;
        }
    }

    @PostMapping("/customer")
    public Customer addCustomer(@RequestBody Customer customer)
    {
        try {
            customerRepo.save(customer);
            log.info("The new Customer has been added to the database... Customer: "+customer);
            return customer;
        }
        catch (Exception e)
        {
            log.error(e);
            return null;
        }
    }
    @PutMapping("/customer/{id}")
    public Customer updateCustomer(@PathVariable String id,@RequestBody Customer customer)
    {
        try{
            Customer temp=customerRepo.getById(Integer.parseInt(id));
            if(temp!=null)
            {
                temp.setName(customer.getName());
                temp.setEmail(customer.getEmail());
                customerRepo.save(temp);
                log.info("The Customer with id "+id+" has been updated: "+temp);
                return temp;
            }
            else {
                throw new RuntimeException("The customer with id "+id+" is not present in Database");
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
    public Issue issueBook(@RequestParam int book_id,@RequestParam int customer_id)
    {
        try{
            Customer customer=customerRepo.getById(customer_id);
            Book book=bookRepo.getById(book_id);
            if(book!=null && customer!=null)
            {
                book.setAvailable(false);
                book=updateBook(String.valueOf(book_id),book);
                Issue issue=new Issue();
                issue.setBook(book);
                issue.setCustomer(customer);
                issueRepo.save(issue);
                log.info("The Book "+book+" has been issued to "+customer);
                return issue;
            }
            else if(book==null)
                throw new RuntimeException("Book with id "+book_id+" is not present in the Database");
            else if(customer==null)
                throw new RuntimeException("Customer with id "+customer_id+" is not present in the Database");
            else {
                throw new RuntimeException("The Book with id "+book_id+" and customer with id "+customer_id+" is not present in Database");
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

}
