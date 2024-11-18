package com.leilao.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import com.leilao.backend.commom.entities.BaseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
@SQLDelete(sql = "UPDATE user SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class User extends BaseEntity implements UserDetails {

    @Column
    private String phone;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "email_confirmed_at")
    private LocalDateTime emailConfirmedAt;

    @Column(name = "external_customer_id")
    private String externalCustomerId;

    @Column(name = "forgot_password_token")
    private String forgotPasswordToken;

    @Column
    private LocalDateTime validationCodeValidity;

    @Column
    private Boolean active;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @Setter(AccessLevel.NONE)
    private List<UserProfile> userProfiles;

    @NotBlank(message = "{name.required}")
    private String name;

    @Email(message = "{name.invalid}")
    private String email;

    // @CPF
    private String cpf;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Transient
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void setPassword(String password) {
        this.password = passwordEncoder.encode(password);
    }

    @JsonIgnore
    @Column(name = "validation_code")
    private String validationCode;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }
}
