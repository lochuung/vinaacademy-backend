package com.vinaacademy.platform.feature.order_payment.utils;

import java.time.LocalDateTime;

import com.vinaacademy.platform.feature.order_payment.entity.Coupon;

public class Utils {
	
	public static boolean isCouponValid(Coupon coupon) {
        LocalDateTime now = LocalDateTime.now(); 
        if (coupon.getStartedAt() != null && coupon.getStartedAt().isAfter(now)) {
            return false; 
        }
        if (coupon.getExpiredAt() != null && coupon.getExpiredAt().isBefore(now)) {
            return false; 
        }
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            return false; 
        }
        return true;
    }
}
