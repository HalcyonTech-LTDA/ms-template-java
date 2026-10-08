package com.example.templatejava.customer.infrastructure.database.adapter;

import com.example.templatejava.customer.domain.exception.CustomerAlreadyExistsException;
import com.example.templatejava.customer.domain.model.Customer;
import com.example.templatejava.customer.domain.model.Customer.CustomerStatus;
import com.example.templatejava.customer.domain.repository.CustomerRepository;
import com.example.templatejava.customer.infrastructure.database.entity.CustomerMongoEntity;
import com.example.templatejava.customer.infrastructure.database.mapper.CustomerDatabaseMapper;
import com.example.templatejava.customer.infrastructure.database.repository.SpringDataCustomerRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;

public class CustomerDatabaseAdapter implements CustomerRepository {

    private final SpringDataCustomerRepository springDataRepository;
    private final CustomerDatabaseMapper customerDatabaseMapper;

    public CustomerDatabaseAdapter(
            SpringDataCustomerRepository springDataRepository,
            CustomerDatabaseMapper customerDatabaseMapper) {
        this.springDataRepository =
                Objects.requireNonNull(
                        springDataRepository, "springDataRepository must not be null");
        this.customerDatabaseMapper =
                Objects.requireNonNull(
                        customerDatabaseMapper, "customerDatabaseMapper must not be null");
    }

    @Override
    public Customer save(Customer customer) {
        Objects.requireNonNull(customer, "customer must not be null");
        CustomerMongoEntity entity = customerDatabaseMapper.toEntity(customer);
        try {
            CustomerMongoEntity savedEntity = springDataRepository.save(entity);
            return customerDatabaseMapper.toDomain(savedEntity);
        } catch (DuplicateKeyException e) {
            throw new CustomerAlreadyExistsException(customer.getEmail());
        }
    }

    @Override
    public Optional<Customer> findById(String id) {
        Objects.requireNonNull(id, "id must not be null");
        return springDataRepository.findById(id).map(customerDatabaseMapper::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        Objects.requireNonNull(email, "email must not be null");
        return springDataRepository.findByEmail(email).map(customerDatabaseMapper::toDomain);
    }

    @Override
    public List<Customer> findByStatus(CustomerStatus status) {
        Objects.requireNonNull(status, "status must not be null");
        return springDataRepository.findByStatus(status.name()).stream()
                .map(customerDatabaseMapper::toDomain)
                .toList();
    }
}
