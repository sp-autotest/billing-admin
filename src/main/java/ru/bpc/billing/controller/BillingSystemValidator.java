package ru.bpc.billing.controller;

import org.springframework.stereotype.Service;
import ru.bpc.billing.controller.dto.carrier.dto.BillingSystemDto;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service
public class BillingSystemValidator {

    public void validateDto(BillingSystemDto dto) throws Exception {
        if (dto.getMaskRegexp() != null)
            checkRegexp(dto.getMaskRegexp());
    }


    private void checkRegexp(String maskRegexp) throws Exception {
        try {
            Pattern.compile(maskRegexp);
        } catch (PatternSyntaxException e) {
            throw new Exception("File mask regexp is invalid");
        }
    }
}
