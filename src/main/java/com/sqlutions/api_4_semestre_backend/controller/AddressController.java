package com.sqlutions.api_4_semestre_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.dto.AddressHeatMap;
import com.sqlutions.api_4_semestre_backend.entity.Address;
import com.sqlutions.api_4_semestre_backend.service.AddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.sqlutions.api_4_semestre_backend.service.TimeService;

@RestController
@RequestMapping("/address")
@Tag(name = "Address", description = "Endpoints for managing addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private TimeService timeService;

    @PostMapping
    @Operation(summary = "Create a new address")
    @ApiResponse(responseCode = "200", description = "Address created successfully")
    public Address createAddress(@RequestBody Address address) {
        return addressService.saveAddress(address);
    }

    @GetMapping
    @Operation(summary = "List all addresses")
    @ApiResponse(responseCode = "200", description = "List of addresses retrieved successfully")
    public List<Address> listAddress() {
        return addressService.listAddress();
    }

    @GetMapping("/heatmap")
    public List<AddressHeatMap> listAddressHeatMapData(
            @RequestParam(defaultValue="20") int minutes,
            @RequestParam(required = false) java.time.LocalDateTime timestamp) {
        return addressService.listAddressHeatMapData(minutes, timestamp == null ? timeService.getCurrentTimeClampedToDatabase() : timestamp);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get address by ID")
    @ApiResponse(responseCode = "200", description = "Address retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Address not found")
    public Address searchAddress(@PathVariable Long id) {
        return addressService.searchAddressById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing address")
    @ApiResponse(responseCode = "200", description = "Address updated successfully")
    @ApiResponse(responseCode = "404", description = "Address not found")
    public Address updateAddress(@PathVariable Long id, @RequestBody Address address) {
        return addressService.updateAddress(id, address);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address")
    @ApiResponse(responseCode = "200", description = "Address deleted successfully")
    @ApiResponse(responseCode = "404", description = "Address not found")
    public void deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
    }
}
