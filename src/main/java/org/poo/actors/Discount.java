package org.poo.actors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class Discount {
    private double ammount = 0.0;
    private String commerciantType = "";
    private boolean active = false;
    private boolean used = false;
}
