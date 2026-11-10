// All calls to the Spring Boot REST API live in this file.
const BASE_URL = '/api/employees';

async function request(url, options = {}) {
  let response;
  try {
    response = await fetch(url, {
      headers: { 'Content-Type': 'application/json' },
      ...options,
    });
  } catch {
    throw new Error('Cannot reach the server. Is the backend running on port 8080?');
  }

  // 204 No Content (successful delete) has no body.
  if (response.status === 204) {
    return null;
  }

  let body = null;
  try {
    body = await response.json();
  } catch {
    body = null;
  }

  if (!response.ok) {
    // The backend always returns { message, fieldErrors? } for errors.
    const error = new Error(body?.message || 'Request failed');
    error.status = response.status;
    error.fieldErrors = body?.fieldErrors || {};
    throw error;
  }
  return body;
}

export const getEmployees = (search = '') =>
  request(search ? `${BASE_URL}?search=${encodeURIComponent(search)}` : BASE_URL);

export const createEmployee = (employee) =>
  request(BASE_URL, { method: 'POST', body: JSON.stringify(employee) });

export const updateEmployee = (id, employee) =>
  request(`${BASE_URL}/${id}`, { method: 'PUT', body: JSON.stringify(employee) });

export const deleteEmployee = (id) => request(`${BASE_URL}/${id}`, { method: 'DELETE' });
