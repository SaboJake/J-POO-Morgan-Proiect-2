package org.poo.banking;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.User;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
public class Bank {
    private List<User> users;

    public Bank() {
        users = new ArrayList<>();
    }
}
