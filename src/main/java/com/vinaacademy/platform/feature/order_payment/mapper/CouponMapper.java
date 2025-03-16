package com.vinaacademy.platform.feature.order_payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vinaacademy.platform.feature.order_payment.dto.CouponDto;
import com.vinaacademy.platform.feature.order_payment.dto.CouponRequest;
import com.vinaacademy.platform.feature.order_payment.entity.Coupon;

@Mapper(componentModel = "spring")
public interface CouponMapper {
	
	@Mapping(target = "carts", ignore = true)
    Coupon toEntity(CouponRequest couponRequest);
   
    CouponDto toDTO(Coupon coupon);
}  