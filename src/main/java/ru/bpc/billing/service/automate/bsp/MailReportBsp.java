package ru.bpc.billing.service.automate.bsp;

import lombok.Getter;
import ru.bpc.billing.domain.BillingSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MailReportBsp {
    private List<BillingSystem> bsList = new ArrayList<>();
    private final List<MailReportUnitBsp> mailReportUnits = new ArrayList<>();


    public void addFoundSystems(List<BillingSystem> bsList) {
        this.bsList.addAll(bsList);
    }

    public void addUnit(MailReportUnitBsp mailReportUnit) {
        mailReportUnits.add(mailReportUnit);
    }

    public void addErrorUnit(MailReportUnitBsp mailReportUnit) {
        mailReportUnits.add(mailReportUnit);
    }

    public String getSubject() {
        return "Результат обработки";
    }

    public String getBody() {
        StringBuilder sb = new StringBuilder();
        String bsListString = bsList.stream().map(BillingSystem::toStringEmail).collect(Collectors.toList()).toString();
        sb.append("Обнаружены биллинговые системы для обработки: ").append(bsListString);
        for (MailReportUnitBsp each : mailReportUnits) {
            sb.append("\n");
            sb.append(each.getReport());
        }
        return sb.toString();
    }
}
