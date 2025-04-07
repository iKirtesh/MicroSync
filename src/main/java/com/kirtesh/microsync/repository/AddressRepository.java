package com.kirtesh.microsync.repository;

import com.kirtesh.microsync.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByIdAndUserId(Long addressId, Long id);
    List<Address> findAllByUserId(Long id);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void unsetDefaultForUser(@Param("userId") Long userId);

    @Query("SELECT a FROM Address a WHERE a.user.username = :username")
    List<Address> findByUsername(@Param("username") String username);
}
