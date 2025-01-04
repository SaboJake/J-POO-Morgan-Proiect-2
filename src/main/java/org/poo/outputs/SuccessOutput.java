package org.poo.outputs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor
public class SuccessOutput {
    private String success;
    private int timestamp;
}
