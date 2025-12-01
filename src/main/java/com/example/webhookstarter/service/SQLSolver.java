package com.example.webhookstarter.service;

import org.springframework.stereotype.Service;

@Service
public class SQLSolver {

    /**
     * Determine which final SQL query to return based on whether regNo's last two
     * digits are odd or even. In a real qualifier this would parse the question
     * PDF and compute the final SQL. Here we return prepared final queries.
     */
    public String finalQueryForRegNo(String regNo) {
        if (regNo == null || regNo.length() < 2) {
            throw new IllegalArgumentException("regNo must contain at least two characters");
        }

        // Extract last two numerical digits from regNo; fall back to numeric chars
        String digits = regNo.replaceAll("^.*?(\\d{1,2})$", "$1");
        int val;
        try {
            val = Integer.parseInt(digits);
        } catch (NumberFormatException ex) {
            // If cannot parse, default to odd path
            val = 1;
        }

        if ((val % 2) == 0) {
            // Placeholder SQL for Question 2 (even regNo) — not available in the environment
            return "-- SQL answer for Question 2 (even regNo)\n-- (replace this placeholder with the real final SQL for Question 2 if needed)";
        } else {
            // Final SQL answer for Question 1 (odd regNo) — PostgreSQL dialect
            // Description: For every department, compute average age of employees
            // who have at least one payment > 70000 and list up to 10 such employee names.
            return "WITH qualifying_employees AS (\n" +
                    "  SELECT DISTINCT e.emp_id, e.department, e.first_name || ' ' || e.last_name AS full_name, e.dob\n" +
                    "  FROM employee e JOIN payments p ON p.emp_id = e.emp_id\n" +
                    "  WHERE p.amount > 70000\n" +
                    "), per_dept AS (\n" +
                    "  SELECT department AS dept_id, AVG(date_part('year', age(current_date, dob)))::numeric AS avg_age,\n" +
                    "    array_agg(full_name ORDER BY emp_id) AS names_arr\n" +
                    "  FROM qualifying_employees GROUP BY department\n" +
                    ")\n" +
                    "SELECT d.department_name AS department_name, ROUND(per_dept.avg_age::numeric, 2) AS average_age,\n" +
                    "  COALESCE(array_to_string(per_dept.names_arr[1:10], ', '), '') AS employee_list\n" +
                    "FROM department d LEFT JOIN per_dept ON per_dept.dept_id = d.department_id\n" +
                    "ORDER BY d.department_id DESC;";
        }
    }
}
