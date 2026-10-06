package com.mobile.billing.infrastructure.adapter.out.persistence.seeds;

import com.mobile.billing.domain.model.UserRole;
import com.mobile.billing.infrastructure.adapter.out.persistence.SpringDataTenantRepository;
import com.mobile.billing.infrastructure.adapter.out.persistence.SpringDataUserRepository;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.TenantEntity;
import com.mobile.billing.infrastructure.adapter.out.persistence.entities.UserEntity;
import java.math.BigDecimal;
import java.util.EnumSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1)
public class InitialAdminUserSeeder implements CommandLineRunner {

    private final SpringDataTenantRepository tenantRepository;
    private final SpringDataUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public InitialAdminUserSeeder(
            SpringDataTenantRepository tenantRepository,
            SpringDataUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed.admin.username:admin@localhost}") String username,
            @Value("${app.seed.admin.password:ChangeMe123!}") String password) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(String... args) {
        TenantEntity tenant = tenantRepository.findAll().stream()
                .findFirst()
                .orElseGet(this::createInitialTenant);

        if (!userRepository.existsByUsername(username)) {
            UserEntity admin = new UserEntity(
                    null,
                    username,
                    passwordEncoder.encode(password),
                    EnumSet.of(UserRole.ADMIN),
                    true);
            admin.setTenantId(tenant.getId());
            userRepository.save(admin);
        }
    }

    private TenantEntity createInitialTenant() {
        TenantEntity tenant = new TenantEntity();
        tenant.setCountryCode("CL");
        tenant.setCompanyName("Tenant inicial");
        tenant.setTaxId("INITIAL-TENANT");
        tenant.setTaxRate(BigDecimal.ZERO);
        tenant.setCurrencyCode("CLP");
        tenant.setAddress("Sin dirección");
        tenant.setPhone("000000000");
        tenant.setActive(true);
        return tenantRepository.save(tenant);
    }
}