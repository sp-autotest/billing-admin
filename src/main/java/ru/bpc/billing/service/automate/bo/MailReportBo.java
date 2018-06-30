package ru.bpc.billing.service.automate.bo;

import lombok.Getter;
import ru.bpc.billing.domain.BillingSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MailReportBo {
    private List<BillingSystem> bsList = new ArrayList<>();
    private final List<MailReportUnitBo> mailReportUnits = new ArrayList<>();


    public void addFoundSystems(List<BillingSystem> bsList) {
        this.bsList.addAll(bsList);
    }

    public void addUnit(MailReportUnitBo mailReportUnit) {
        mailReportUnits.add(mailReportUnit);
    }

    public void addErrorUnit(MailReportUnitBo mailReportUnit) {
        mailReportUnits.add(mailReportUnit);
    }

    public String getSubject() {
        return "Результат обработки";
    }

    public String getBody() {
        StringBuilder sb = new StringBuilder();
        String bsListString = bsList.stream().map(BillingSystem::toStringEmail).collect(Collectors.toList()).toString();
        //sb.append("Обнаружены биллинговые системы для обработки: ").append(bsListString);
        for (MailReportUnitBo each : mailReportUnits) {
            sb.append("\n");
            sb.append(each.getReport());
        }
        return sb.toString();
    }
}
