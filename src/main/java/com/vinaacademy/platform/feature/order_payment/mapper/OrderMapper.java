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
    @Mapping(target = "coupon_id", expression = "java(order.getCoupon() != null ? order.getCoupon().getId() : null)")
	@Mapping(target = "payment_id", expression = "java(order.getPayment() != null ? order.getPayment().getId() : null)")
	OrderDto toOrderDto(Order order); 
 
    Order toEntity(OrderDto orderDto);

    OrderRequest toOrderRequest(OrderDto orderDto);
}
