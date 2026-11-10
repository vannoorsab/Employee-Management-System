import { useCallback, useEffect, useState } from 'react';
import { createEmployee, deleteEmployee, getEmployees, updateEmployee } from './api.js';
import EmployeeForm from './components/EmployeeForm.jsx';
import EmployeeTable from './components/EmployeeTable.jsx';

export default function App() {
  const [employees, setEmployees] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState(null); // { type: 'success' | 'error', text }
  const [editingEmployee, setEditingEmployee] = useState(null);
  const [fieldErrors, setFieldErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [formCounter, setFormCounter] = useState(0); // changes after each save to reset the form

  const loadEmployees = useCallback(async (keyword) => {
    setLoading(true);
    try {
      setEmployees(await getEmployees(keyword));
    } catch (error) {
      setMessage({ type: 'error', text: error.message });
    } finally {
      setLoading(false);
    }
  }, []);

  // Reload the list when the search text changes (waits 300 ms after typing stops).
  useEffect(() => {
    const timer = setTimeout(() => loadEmployees(search), 300);
    return () => clearTimeout(timer);
  }, [search, loadEmployees]);

  const resetForm = () => {
    setEditingEmployee(null);
    setFieldErrors({});
    setFormCounter((count) => count + 1);
  };

  const handleSubmit = async (values) => {
    setSubmitting(true);
    setFieldErrors({});
    setMessage(null);
    try {
      if (editingEmployee) {
        await updateEmployee(editingEmployee.id, values);
        setMessage({ type: 'success', text: 'Employee updated.' });
      } else {
        await createEmployee(values);
        setMessage({ type: 'success', text: 'Employee added.' });
      }
      resetForm();
      await loadEmployees(search);
    } catch (error) {
      // Validation errors from the backend are shown under each field.
      setFieldErrors(error.fieldErrors || {});
      setMessage({ type: 'error', text: error.message });
    } finally {
      setSubmitting(false);
    }
  };

  const handleEdit = (employee) => {
    setEditingEmployee(employee);
    setFieldErrors({});
    setMessage(null);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleDelete = async (employee) => {
    if (!window.confirm(`Delete ${employee.firstName} ${employee.lastName}?`)) {
      return;
    }
    try {
      await deleteEmployee(employee.id);
      setMessage({ type: 'success', text: 'Employee deleted.' });
      if (editingEmployee?.id === employee.id) {
        resetForm();
      }
      await loadEmployees(search);
    } catch (error) {
      setMessage({ type: 'error', text: error.message });
    }
  };

  return (
    <main className="container">
      <h1>Employee Management System</h1>

      {message && <div className={`banner ${message.type}`}>{message.text}</div>}

      <EmployeeForm
        key={editingEmployee ? `edit-${editingEmployee.id}` : `new-${formCounter}`}
        editingEmployee={editingEmployee}
        fieldErrors={fieldErrors}
        submitting={submitting}
        onSubmit={handleSubmit}
        onCancel={resetForm}
      />

      <section className="card">
        <div className="list-header">
          <h2>Employees ({employees.length})</h2>
          <input
            type="search"
            placeholder="Search by name, email or department"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />
        </div>
        <EmployeeTable employees={employees} loading={loading} onEdit={handleEdit} onDelete={handleDelete} />
      </section>
    </main>
  );
}
