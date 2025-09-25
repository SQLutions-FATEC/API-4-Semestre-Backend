package com.sqlutions.api_4_semestre_backend.entity;

import jakarta.persistence.Column; 
import jakarta.persistence.Entity; 
import jakarta.persistence.GeneratedValue; 
import jakarta.persistence.GenerationType; 
import jakarta.persistence.Id; 
import jakarta.persistence.Table;

@Entity
@Table(name = "adress")
public class Adress {

    @Id     
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adr_id")
    private Long id;

    @Column(nullable = false, name = "adr_adress")
    private String adress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}
