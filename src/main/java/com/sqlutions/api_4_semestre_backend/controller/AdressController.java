package com.sqlutions.api_4_semestre_backend.controller;

import com.sqlutions.api_4_semestre_backend.entity.Adress;
import com.sqlutions.api_4_semestre_backend.service.AdressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adress")
public class AdressController {

    @Autowired
    private AdressService adressService;

    @PostMapping
    public Adress createAdress(@RequestBody Adress adress) {
        return adressService.saveAdress(adress);
    }

    @GetMapping
    public List<Adress> listAdress() {
        return adressService.listAdress();
    }

    @GetMapping("/{id}")
    public Adress searchAdress(@PathVariable Long id) {
        return adressService.searchAdressById(id);
    }

    @PutMapping("/{id}")
    public Adress updateAdress(@PathVariable Long id, @RequestBody Adress adress) {
        return adressService.updateAdress(id, adress);
    }

    @DeleteMapping("/{id}")
    public void deleteAdress(@PathVariable Long id) {
        adressService.deleteAdress(id);
    }
}
