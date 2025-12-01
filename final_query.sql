-- Final SQL answer (PostgreSQL) for Question 1 (odd regNo)
WITH qualifying_employees AS (
  SELECT DISTINCT e.emp_id, e.department, e.first_name || ' ' || e.last_name AS full_name, e.dob
  FROM employee e JOIN payments p ON p.emp_id = e.emp_id
  WHERE p.amount > 70000
), per_dept AS (
  SELECT department AS dept_id, AVG(date_part('year', age(current_date, dob)))::numeric AS avg_age,
    array_agg(full_name ORDER BY emp_id) AS names_arr
  FROM qualifying_employees GROUP BY department
)
SELECT d.department_name AS department_name, ROUND(per_dept.avg_age::numeric, 2) AS average_age,
  COALESCE(array_to_string(per_dept.names_arr[1:10], ', '), '') AS employee_list
FROM department d LEFT JOIN per_dept ON per_dept.dept_id = d.department_id
ORDER BY d.department_id DESC;
