package com.vinaacademy.platform.feature.order_payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vinaacademy.platform.feature.order_payment.dto.OrderDto;
import com.vinaacademy.platform.feature.order_payment.dto.OrderItemDto;
import com.vinaacademy.platform.feature.order_payment.dto.OrderRequest;
import com.vinaacademy.platform.feature.order_payment.entity.Order;
import com.vinaacademy.platform.feature.order_payment.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {
		  
	
	@Mapping(target = "user_id", source = "user.id")
    @Mapping(target = "coupon_id", source = "coupon.id")
	@Mapping(target = "payment_id", expression = "java(payment.getPayment() != null ? payment.getPayment().getId() : null)")
	OrderDto toOrderDto(Order order); 
 
    Order toEntity(OrderDto orderDto);

    OrderRequest toOrderRequest(OrderDto orderDto);
}
