package com.example.ecommerce.api.port_one;

import com.example.ecommerce.common.exception.port_one.PortOneException;
import com.example.ecommerce.common.exception.port_one.PortOneNotFoundPaymentException;
import com.example.ecommerce.dto.port_one.PortOneGetPaymentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@AllArgsConstructor
@Setter
public class PortOnePayment {

    private final static String PORT_ONE_BASE_URL = "https://api.portone.io";

    private final RestTemplate restTemplate;
    private final ApiAuthProperties apiAuthProperties;

    public PortOneGetPaymentResponseDto getPayment(String paymentId){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "PortOne " + apiAuthProperties.getToken());

        String url = PORT_ONE_BASE_URL + "/payments/" + paymentId;

        HttpEntity<String> entity = new HttpEntity<>(headers);

        PortOneGetPaymentResponseDto responseDto;

        try {
            ResponseEntity<PortOneGetPaymentResponseDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, PortOneGetPaymentResponseDto.class);
            responseDto = response.getBody();
            log.info("responseDto: " + responseDto);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new PortOneNotFoundPaymentException(PortOneException.NOTFOUND.getStatus(), PortOneException.NOTFOUND.getMessage());
        }

        return responseDto;
    }

}
