package com.ajtech.mail.batch.mapper;

import com.ajtech.mail.batch.domain.Customer;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class CustomerMapper implements FieldSetMapper<Customer> {

    public static final int ACT_NBR = 0;
    public static final int  BANK_ACT_NBR = 1;
    public static final int CARD_MEMBER_NAME = 2;
    public static final int PAYMENT_DUE_DATE = 3;
    @Override
    public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
        Customer customer=new Customer();
        customer.setActNbr(fieldSet.readRawString(ACT_NBR));
        customer.setActNbr(fieldSet.readRawString(BANK_ACT_NBR));
        customer.setActNbr(fieldSet.readRawString(CARD_MEMBER_NAME));
        customer.setActNbr(fieldSet.readRawString(PAYMENT_DUE_DATE));
        return customer;
    }
}