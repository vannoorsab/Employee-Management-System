import { useState } from 'react';

const FIELDS = [
  { name: 'firstName', label: 'First name', type: 'text' },
  { name: 'lastName', label: 'Last name', type: 'text' },
  { name: 'email', label: 'Email', type: 'email' },
  { name: 'department', label: 'Department', type: 'text' },
  { name: 'jobTitle', label: 'Job title', type: 'text' },
  { name: 'salary', label: 'Salary (INR)', type: 'number' },
  { name: 'joiningDate', label: 'Joining date', type: 'date' },
];

function toFormValues(employee) {
  return {
    firstName: employee?.firstName ?? '',
    lastName: employee?.lastName ?? '',
    email: employee?.email ?? '',
    department: employee?.department ?? '',
    jobTitle: employee?.jobTitle ?? '',
    salary: employee ? String(employee.salary) : '',
    joiningDate: employee?.joiningDate ?? '',
  };
}

// Used for both "add" and "edit". The parent changes the `key` prop
// when the form should start fresh, which resets this component's state.
export default function EmployeeForm({ editingEmployee, fieldErrors, submitting, onSubmit, onCancel }) {
  const [values, setValues] = useState(() => toFormValues(editingEmployee));

  const handleChange = (event) => {
    const { name, value } = event.target;
    setValues((previous) => ({ ...previous, [name]: value }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    onSubmit({
      ...values,
      salary: values.salary === '' ? null : Number(values.salary),
    });
  };

  return (
    <form className="card form" onSubmit={handleSubmit} noValidate>
      <h2>{editingEmployee ? 'Edit employee' : 'Add employee'}</h2>

      <div className="form-grid">
        {FIELDS.map((field) => (
          <label key={field.name} className="field">
            <span>{field.label}</span>
            <input
              name={field.name}
              type={field.type}
              value={values[field.name]}
              onChange={handleChange}
              step={field.type === 'number' ? '0.01' : undefined}
              className={fieldErrors[field.name] ? 'invalid' : ''}
            />
            {fieldErrors[field.name] && <small className="error-text">{fieldErrors[field.name]}</small>}
          </label>
        ))}
      </div>

      <div className="form-actions">
        <button type="submit" className="btn primary" disabled={submitting}>
          {submitting ? 'Saving...' : editingEmployee ? 'Update' : 'Add'}
        </button>
        {editingEmployee && (
          <button type="button" className="btn" onClick={onCancel}>
            Cancel
          </button>
        )}
      </div>
    </form>
  );
}
