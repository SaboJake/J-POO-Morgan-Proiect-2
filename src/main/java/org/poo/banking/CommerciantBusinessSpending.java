package org.poo.banking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.poo.utils.RoundingSerializer;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
public class CommerciantBusinessSpending {
    private String commerciant;
    @JsonSerialize(using = RoundingSerializer.class)
    @JsonProperty("total received")
    private double totalReceived;
    private List<String> managers;
    private List<String> employees;
    @JsonIgnore
    private List<String> managerEmails;
    @JsonIgnore
    private List<String> employeeEmails;

    public CommerciantBusinessSpending(final String commerciant, final double totalReceived) {
        this.commerciant = commerciant;
        this.totalReceived = totalReceived;
        this.managers = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.managerEmails = new ArrayList<>();
        this.employeeEmails = new ArrayList<>();
    }
}
