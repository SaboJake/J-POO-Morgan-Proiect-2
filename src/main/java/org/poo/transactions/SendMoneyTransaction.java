package org.poo.transactions;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class SendMoneyTransaction extends Transaction {
    private String senderIBAN;
    private String receiverIBAN;
    private String amount;
    private String transferType;

    public SendMoneyTransaction(final int timestamp, final String description,
                                final String senderIBAN, final String receiverIBAN,
                                final String amount, final String transferType) {
        super(timestamp, description);
        this.senderIBAN = senderIBAN;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.transferType = transferType;
        this.type = "sendMoney";
    }

    public SendMoneyTransaction(final SendMoneyTransaction transaction) {
        super(transaction.getTimestamp(), transaction.getDescription());
        this.senderIBAN = transaction.getSenderIBAN();
        this.receiverIBAN = transaction.getReceiverIBAN();
        this.amount = transaction.getAmount();
        this.transferType = transaction.getTransferType();
        this.type = transaction.getType();
    }
}
