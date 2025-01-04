package org.poo.actors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.poo.actors.cardState.CardState;

@Setter @Getter @AllArgsConstructor
public class Card {
    private String cardNumber;
    private String status;
    @JsonIgnore
    private CardState state;
    @JsonIgnore
    private boolean oneTime;

    /**
     * Handles the card
     */
    public void handle() {
        state.handle(this);
    }
}
