package org.poo.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class BankCommand implements Command {
    protected String command;
    protected Object output;
    protected int timestamp;

    /**
     * Execute
     */
    public void execute() {
    }
}
