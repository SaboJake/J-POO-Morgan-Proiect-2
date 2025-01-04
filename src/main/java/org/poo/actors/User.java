package org.poo.actors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.poo.transactions.Transaction;

import java.util.List;

@Setter @Getter @AllArgsConstructor
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private List<Account> accounts;
    @JsonIgnore
    private List<Transaction> transactions;
}
