package com.aditi.dripyard.model;

import com.aditi.dripyard.domain.AccountStatus;
import com.aditi.dripyard.domain.USER_ROLE;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String sellerName;

    private String mobile;

    @Column(unique = true , nullable = false)
    private String email;
    private String password;

    @Embedded
    private BusinessDetails businessDetails= new BusinessDetails();


    @Embedded
    private BankDetails bankDetails= new BankDetails();

    @OneToOne(cascade = CascadeType.ALL)
    private Address pickupAddress = new Address();

    private String GSTIN;


    private USER_ROLE role = USER_ROLE.ROLE_SELLER;

    public boolean isEmailVerified = false;

    private AccountStatus accountStatus = AccountStatus.PENDING_VERIFICATION;



}
