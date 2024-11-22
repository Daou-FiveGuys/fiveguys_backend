package com.precapstone.fiveguys_backend.api.amountused;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "amount_used")
public class AmountUsed {
}
