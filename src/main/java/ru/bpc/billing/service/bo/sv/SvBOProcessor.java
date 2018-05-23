package ru.bpc.billing.service.bo.sv;

import org.springframework.context.i18n.LocaleContextHolder;
import ru.bpc.billing.domain.bo.sv.SvBORevenue;
import ru.bpc.billing.service.ISystem;
import ru.bpc.billing.service.bo.AbstractBOProcessor;

/**
 * User: Krainov
 * Date: 15.08.14
 * Time: 12:20
 */
public class SvBOProcessor extends AbstractBOProcessor {

    @Override
    protected Class[] getBOClasses() {
        return new Class[]{SvBORevenue.class};
    }

    @Override
    public String getSystemName() {
        return messageSource.getMessage(ISystem.SYSTEM_PREFIX + "bo.processor.revenueSv",new Object[]{},
                "BO revenue Smart Vista processor", LocaleContextHolder.getLocale());
    }
}
