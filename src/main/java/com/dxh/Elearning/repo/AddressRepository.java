package com.dxh.Elearning.repo;

import com.dxh.Elearning.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Set<Address> findByUser_Id(Long userId);

    boolean existsByUser_IdAndCityAndDistrictAndWardsAndSpecificAddressAndFullName(Long id, String city, String district, String wards, String specificAddress, String fullName);

    Optional<Address> findByIdAndUser_Id(Long addrId, Long id);

    void deleteByIdAndUser_Id(Long addrId, Long userId);
    Optional<Address> findByUser_IdAndIsDefaultTrue(Long userId);

}
