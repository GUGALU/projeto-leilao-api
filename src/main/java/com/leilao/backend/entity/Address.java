package com.leilao.backend.entity;

import com.leilao.backend.commom.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "address")
@SQLDelete(sql = "UPDATE address SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Address extends BaseEntity {
    
    @Column
    private String street;

    @Column
    private String number;
    
    @Column
    private String complement;
    
    @Column
    private String neighborhood;
    
    @Column
    private String city;
    
    @Column
    private String state;
    
    @Column
    private String country;

    @Override
    public String getUsername() {
        return "";
    }
}
