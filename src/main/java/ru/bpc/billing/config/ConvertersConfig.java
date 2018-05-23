package ru.bpc.billing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import ru.bpc.billing.converter.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Smirnov_Y on 05.04.2016.
 */
@Configuration
public class ConvertersConfig {

    @Bean
    public CarrierToDtoConverter carrierToDtoConverter() {
        CarrierToDtoConverter carrierToDtoConverter = new CarrierToDtoConverter();
        return carrierToDtoConverter;
    }

    @Bean
    public TerminalToDtoConverter countryToDtoConverter() {
        TerminalToDtoConverter terminalToDtoConverter = new TerminalToDtoConverter();
        return terminalToDtoConverter;
    }

    @Bean
    public CarrierToEntityConverter carrierToEntityConverter() {
        CarrierToEntityConverter carrierToEntityConverter = new CarrierToEntityConverter();
        return carrierToEntityConverter;
    }

    @Bean
    public TerminalToEntityConverter countryToEntityConverter() {
        TerminalToEntityConverter terminalToEntityConverter = new TerminalToEntityConverter();
        return terminalToEntityConverter;
    }

    @Bean
    public CarrierFilterToEntityConverter carrierFilterToEntityConverter() {
        CarrierFilterToEntityConverter carrierFilterToEntityConverter = new CarrierFilterToEntityConverter();
        return carrierFilterToEntityConverter;
    }

    @Bean
    public TerminalFilterToEntityConverter countryFilterToEntityConverter() {
        TerminalFilterToEntityConverter terminalFilterToEntityConverter = new TerminalFilterToEntityConverter();
        return terminalFilterToEntityConverter;
    }

    @Bean
    public CurrencyFilterToEntityConverter currencyFilterToEntityConverter() {
        CurrencyFilterToEntityConverter currencyFilterToEntityConverter = new CurrencyFilterToEntityConverter();
        return currencyFilterToEntityConverter;
    }

    @Bean
    public CurrencyToDtoConverter currencyToDtoConverter() {
        CurrencyToDtoConverter currencyToDtoConverter = new CurrencyToDtoConverter();
        return currencyToDtoConverter;
    }

    @Bean
    public BillingSystemDtoToEntityConverter billingSystemDtoToEntityConverter() {
        BillingSystemDtoToEntityConverter converter = new BillingSystemDtoToEntityConverter();
        return converter;
    }

    @Bean
    public BillingSystemToDtoConverter billingSystemToDto() {
        BillingSystemToDtoConverter converter = new BillingSystemToDtoConverter();
        return converter;
    }

    @Bean(name="conversionService")
    public FormattingConversionServiceFactoryBean conversionService() {
        FormattingConversionServiceFactoryBean conversionService = new FormattingConversionServiceFactoryBean();
        Set<Converter> converters = new HashSet<>();
        converters.add(carrierToDtoConverter());
        converters.add(countryToDtoConverter());
        converters.add(carrierToEntityConverter());
        converters.add(countryToEntityConverter());
        converters.add(carrierFilterToEntityConverter());
        converters.add(countryFilterToEntityConverter());
        converters.add(currencyFilterToEntityConverter());
        converters.add(currencyToDtoConverter());
        converters.add(billingSystemDtoToEntityConverter());
        converters.add(billingSystemToDto());
        conversionService.setConverters(converters);
        return conversionService;

    }
}
