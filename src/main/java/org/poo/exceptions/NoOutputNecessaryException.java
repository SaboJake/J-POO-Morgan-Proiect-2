package org.poo.exceptions;

import org.poo.commands.BankCommand;

public class NoOutputNecessaryException extends RuntimeException {
    public NoOutputNecessaryException(final String message) {
        super(message);
    }

  /**
   * Print a useful message regarding the command that just failed
   * @param command - the command
   */
  public void handle(final BankCommand command) {
  }
}
