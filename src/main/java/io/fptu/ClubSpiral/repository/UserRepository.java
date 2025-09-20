package io.fptu.ClubSpiral.repository;

import io.fptu.ClubSpiral.model.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT a FROM User a WHERE a.fullName = :input OR a.email = :input")
    Optional<User> findByFullNameOrEmail(@Param("input") String input);

    Optional<User> findByEmail(@NotBlank(message = "signup.email.required") @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "signup.email.invalid"
    ) String email);
}
