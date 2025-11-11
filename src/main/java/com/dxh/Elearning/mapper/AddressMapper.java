package com.dxh.Elearning.mapper;

import com.dxh.Elearning.dto.request.AddressCreateRequest;
import com.dxh.Elearning.dto.response.AddressOrderResponse;
import com.dxh.Elearning.dto.response.AddressResponse;
import com.dxh.Elearning.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toAddress(AddressCreateRequest addressCreateRequest);
    AddressResponse toAddressResponse(Address address);
    AddressOrderResponse toAddressOrderResponse(Address address);
}
