package com.vinaacademy.platform.feature.order_payment.mapper;

import com.vinaacademy.platform.feature.order_payment.dto.PaymentDto;
import com.vinaacademy.platform.feature.order_payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
	
	@Mapping(source = "order.id", target = "orderId")
	@Mapping(target = "urlPayment", ignore = true)
    PaymentDto toDTO(Payment payment);

	List<PaymentDto> toDTOList(List<Payment> payments);
}
