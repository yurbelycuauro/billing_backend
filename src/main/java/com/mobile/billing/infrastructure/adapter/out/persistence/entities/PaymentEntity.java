package com.mobile.billing.infrastructure.adapter.out.persistence.entities;


import com.mobile.billing.domain.model.Payment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
public class PaymentEntity {
   
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "terms_days", nullable = false)
    private Integer termsDays;

    public static PaymentEntity fromDomain(Payment payment) {
        if (payment == null) {
            return null;
        }
        return new PaymentEntity(
            payment.getId(),
            payment.getPaymentMethod(),
            payment.getTermsDays()
        );
    }

    public Payment toDomain() {
        return new Payment(id, paymentMethod, termsDays);
    }
}
