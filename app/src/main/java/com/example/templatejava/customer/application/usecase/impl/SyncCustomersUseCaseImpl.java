package com.example.templatejava.customer.application.usecase.impl;

import com.example.templatejava.customer.application.gateway.CustomerBureauProviderGateway;
import com.example.templatejava.customer.application.usecase.SyncCustomersUseCase;
import com.example.templatejava.customer.domain.model.Customer;
import com.example.templatejava.customer.domain.model.Customer.CustomerStatus;
import com.example.templatejava.customer.domain.repository.CustomerRepository;
import java.util.List;
import java.util.Objects;

public class SyncCustomersUseCaseImpl implements SyncCustomersUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerBureauProviderGateway customerBureauProviderGateway;

    public SyncCustomersUseCaseImpl(
            CustomerRepository customerRepository,
            CustomerBureauProviderGateway customerBureauProviderGateway) {
        this.customerRepository =
                Objects.requireNonNull(customerRepository, "customerRepository must not be null");
        this.customerBureauProviderGateway =
                Objects.requireNonNull(
                        customerBureauProviderGateway,
                        "customerBureauProviderGateway must not be null");
    }

    @Override
    public int execute() {
        List<Customer> pendingCustomers =
                customerRepository.findByStatus(CustomerStatus.PENDING_BUREAU_ENRICHMENT);

        int updatedCount = 0;
        for (Customer customer : pendingCustomers) {
            try {
                CustomerBureauProviderGateway.BureauData bureauData =
                        customerBureauProviderGateway.fetchBureauData(
                                customer.getId(), customer.getEmail());

                CustomerStatus newStatus =
                        "APPROVED".equalsIgnoreCase(bureauData.status())
                                ? CustomerStatus.ACTIVE
                                : CustomerStatus.SUSPENDED;

                customer.enrichWithBureauData(bureauData.score(), newStatus);
                customerRepository.save(customer);
                updatedCount++;
            } catch (Exception e) {
                // Ignore exception to process the remaining customers
            }
        }

        return updatedCount;
    }
}
