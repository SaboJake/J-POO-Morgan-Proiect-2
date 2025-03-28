package org.poo.outputs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor
public class ErrorOutput {
    private String error;
    private int timestamp;
}
