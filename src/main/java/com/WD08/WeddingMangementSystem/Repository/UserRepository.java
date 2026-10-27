package com.WD08.WeddingMangementSystem.Repository;



import com.WD08.WeddingMangementSystem.entity.Admin;
import com.WD08.WeddingMangementSystem.entity.Customer;
import com.WD08.WeddingMangementSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT c FROM Customer c")
    List<Customer> findAllCustomers();

    @Query("SELECT a FROM Admin a")
    List<Admin> findAllAdmins();
}
