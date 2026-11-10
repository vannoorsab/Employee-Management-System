const currency = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' });

export default function EmployeeTable({ employees, loading, onEdit, onDelete }) {
  if (loading) {
    return <p className="muted">Loading...</p>;
  }
  if (employees.length === 0) {
    return <p className="muted">No employees found.</p>;
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Department</th>
            <th>Job title</th>
            <th>Salary</th>
            <th>Joined</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {employees.map((employee) => (
            <tr key={employee.id}>
              <td>{employee.id}</td>
              <td>
                {employee.firstName} {employee.lastName}
              </td>
              <td>{employee.email}</td>
              <td>{employee.department}</td>
              <td>{employee.jobTitle}</td>
              <td>{currency.format(employee.salary)}</td>
              <td>{employee.joiningDate}</td>
              <td className="actions">
                <button className="btn small" onClick={() => onEdit(employee)}>
                  Edit
                </button>
                <button className="btn small danger" onClick={() => onDelete(employee)}>
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
