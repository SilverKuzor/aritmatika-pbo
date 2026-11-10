package com.pbo.demo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.MathContext;

@Entity
public class Operasi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 24, scale = 12)
    private BigDecimal bilangan1;

    @Column(nullable = false, precision = 24, scale = 12)
    private BigDecimal bilangan2;

    @Column(nullable = false, length = 1)
    private String operator;

    protected Operasi() {}

    public void isi(BigDecimal bilangan1, String operator, BigDecimal bilangan2) {
        this.bilangan1 = bilangan1;
        this.operator = operator;
        this.bilangan2 = bilangan2;
    }

    public Long getId() { return id; }
    public String getBilangan1() { return format(bilangan1); }
    public String getBilangan2() { return format(bilangan2); }
    public String getOperator() { return operator; }

    @Transient
    public String getHasil() {
        BigDecimal hasil = switch (operator) {
            case "+" -> bilangan1.add(bilangan2);
            case "-" -> bilangan1.subtract(bilangan2);
            case "*" -> bilangan1.multiply(bilangan2);
            case "/" -> bilangan1.divide(bilangan2, MathContext.DECIMAL128);
            default -> throw new IllegalStateException("Operator tidak valid.");
        };
        return format(hasil);
    }

    private String format(BigDecimal nilai) {
        return nilai.stripTrailingZeros().toPlainString();
    }
}
