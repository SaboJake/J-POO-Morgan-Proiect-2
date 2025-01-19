package org.poo.banking;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.poo.utils.RoundingSerializer;

@Setter @Getter
@AllArgsConstructor
public class AssociateSpending {
    private String username;
    @JsonSerialize(using = RoundingSerializer.class)
    private double spent;
    @JsonSerialize(using = RoundingSerializer.class)
    private double deposited;
}
