package org.poo.outputs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor
public class DefaultOutput {
    private int timestamp;
    private String description;
}
