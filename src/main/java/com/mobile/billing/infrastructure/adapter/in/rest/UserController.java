package com.mobile.billing.infrastructure.adapter.in.rest;

import com.mobile.billing.domain.ports.in.CreateUserUseCase;
import com.mobile.billing.domain.ports.in.DeleteUserUseCase;
import com.mobile.billing.domain.ports.in.GetUserUseCase;
import com.mobile.billing.domain.ports.in.UpdateUserUseCase;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.CreateUserRequest;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.UpdateUserRequest;
import com.mobile.billing.infrastructure.adapter.in.rest.dto.UserResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final CreateUserUseCase createUser;
    private final GetUserUseCase getUsers;
    private final UpdateUserUseCase updateUser;
    private final DeleteUserUseCase deleteUser;

    public UserController(CreateUserUseCase createUser, GetUserUseCase getUsers,
            UpdateUserUseCase updateUser, DeleteUserUseCase deleteUser) {
        this.createUser = createUser;
        this.getUsers = getUsers;
        this.updateUser = updateUser;
        this.deleteUser = deleteUser;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request,
            UriComponentsBuilder uriBuilder) {
        UserResponse response = UserResponse.fromDomain(createUser.execute(request.toCommand()));
        return ResponseEntity.created(uriBuilder.path("/api/v1/users/{id}").build(response.id())).body(response);
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return getUsers.findAll().stream().map(UserResponse::fromDomain).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return getUsers.findById(id).map(UserResponse::fromDomain)
                .map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        try {
            return ResponseEntity.ok(UserResponse.fromDomain(updateUser.execute(id, request.toCommand())));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            deleteUser.execute(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }
}