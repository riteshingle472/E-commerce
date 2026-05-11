package org.riteshingle.ecommerce.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class RazorpayService {

    @Autowired
    private RazorpayClient razorpayClient;

    @Value("${razorpay.key.secret}")
    private String secret;

    public Order createOrder(Integer amount, Long orderId) throws RazorpayException {

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }

        JSONObject orderRequest = new JSONObject();

        orderRequest.put("amount", amount * 100); // in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_" + orderId);
        orderRequest.put("payment_capture", 1);

        JSONObject notes = new JSONObject();
        notes.put("orderId", orderId);
        notes.put("source", "ecommerce");

        orderRequest.put("notes", notes);

        return razorpayClient.orders.create(orderRequest);
    }
}
