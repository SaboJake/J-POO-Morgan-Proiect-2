package org.poo.transactions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
public class NewSplitPaymentTransaction extends Transaction {
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private List<Double> amountForUsers;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Double amount;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String error;

    private List<String> involvedAccounts;
    private String currency;
    private String splitPaymentType;

    public NewSplitPaymentTransaction(final int timestamp, final String description,
                                      final List<String> involvedAccounts, final String currency,
                                      final String splitPaymentType) {
        super(timestamp, description);
        this.involvedAccounts = involvedAccounts;
        this.currency = currency;
        this.splitPaymentType = splitPaymentType;
    }
}
