package com.example.webhookstarter.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SQLSolverTest {

    private final SQLSolver solver = new SQLSolver();

    @Test
    void finalQueryForRegNo_odd() {
        String q = solver.finalQueryForRegNo("REG12347");
        assertThat(q).contains("Question 1");
    }

    @Test
    void finalQueryForRegNo_even() {
        String q = solver.finalQueryForRegNo("REG12348");
        assertThat(q).contains("Question 2");
    }
}
