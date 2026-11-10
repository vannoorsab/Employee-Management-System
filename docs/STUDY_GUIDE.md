# Study guide: Employee Management System

Read this and the code until you can explain every part in your own words. The interviewer will ask about whatever is on your resume.

## 1. The big picture

```
React (browser)  --HTTP/JSON-->  Controller  ->  Service  ->  Repository  -->  Hibernate  -->  MySQL
```

- **React** shows the pages and sends HTTP requests (`fetch`) to `/api/employees`.
- **Controller** receives the request and returns a response. No business logic.
- **Service** holds the rules (duplicate email, "not found") and runs inside a transaction.
- **Repository** talks to the database through Spring Data JPA.
- **Hibernate** (the JPA implementation) turns Java objects into SQL and back.

## 2. Backend classes, one by one

| Class | Job |
|---|---|
| `Employee` (model) | JPA entity. `@Entity` + `@Table` map it to the `employees` table; `@Id` + `@GeneratedValue(IDENTITY)` make the id an auto-increment column; `@Column` sets length, nullability and uniqueness. |
| `EmployeeRequest` (dto) | What the client sends. Its annotations (`@NotBlank`, `@Email`, `@Positive`, `@PastOrPresent`) are the validation rules. |
| `EmployeeResponse` (dto) | What the API returns. Keeps the database entity hidden from clients. |
| `EmployeeRepository` | `extends JpaRepository<Employee, Long>`. Gives save/findById/findAll/delete for free. `existsByEmailIgnoreCase...` are derived queries (Spring builds SQL from the method name). `search` is a custom JPQL query with `@Query`. |
| `EmployeeService` | Business logic. `@Service`, constructor injection, `@Transactional` on methods that change data. |
| `EmployeeController` | `@RestController` + `@RequestMapping("/api/employees")`. `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` map HTTP methods to Java methods. |
| `GlobalExceptionHandler` | `@RestControllerAdvice`. Catches exceptions from any controller and returns a JSON error. |

## 3. What happens on `POST /api/employees`

1. Spring converts the JSON body into an `EmployeeRequest` (Jackson).
2. `@Valid` checks the validation annotations. If any rule fails, Spring throws `MethodArgumentNotValidException` before the controller code runs.
3. The controller calls `service.create(request)`.
4. The service trims and lowercases the email, checks it is not already used, and copies the fields into a new `Employee`.
5. `repository.save(employee)` makes Hibernate run an `INSERT` in MySQL.
6. The service returns an `EmployeeResponse`; the controller replies `201 Created` with a `Location` header.

## 4. Validation vs exception handling

- **Validation** = checking the input is acceptable (`@Valid` + annotations on `EmployeeRequest`).
- **Exception handling** = deciding what the client sees when something goes wrong.
  `GlobalExceptionHandler` maps each exception to a status:
  - `MethodArgumentNotValidException` -> 400 with `fieldErrors`
  - `ResourceNotFoundException` -> 404
  - `DuplicateEmailException` -> 409
  - `HttpMessageNotReadableException` (bad JSON/date) -> 400
  - anything else -> 500 with a generic message (details only go to the log)

## 5. JPA, Hibernate and Spring Data JPA

- **JPA** is a specification (a set of interfaces and annotations) for mapping Java objects to tables.
- **Hibernate** is the implementation of JPA that actually generates SQL.
- **Spring Data JPA** sits on top and creates repository implementations for you.
- `spring.jpa.hibernate.ddl-auto=update` lets Hibernate create or adjust the table at startup. In production you would use migrations (Flyway) instead.

## 6. Frontend

- `api.js` wraps `fetch` for each endpoint and throws an error with `message` and `fieldErrors` when the response is not OK.
- `App.jsx` keeps the state (`useState`): employee list, search text, message, the employee being edited, and field errors. `useEffect` reloads the list when the search text changes (after a 300 ms pause).
- `EmployeeForm` is a **controlled form**: each input's value comes from state, and `onChange` updates it. The `key` prop on the form makes React create a fresh form after each save or when you start editing.
- `vite.config.js` proxies `/api` to `localhost:8080`, so the browser talks to one origin and CORS is not needed during development.

## 7. Questions you may be asked

1. **What is Spring Boot and why use it?** It sets up a Spring application with sensible defaults: auto-configuration, an embedded server, and starter dependencies, so you write less configuration.
2. **`@Controller` vs `@RestController`?** `@RestController` = `@Controller` + `@ResponseBody`: return values are written as JSON instead of being treated as view names.
3. **What is dependency injection?** Spring creates objects (beans) and passes them to the classes that need them. Here the service receives the repository through its constructor.
4. **JPA vs Hibernate?** JPA is the standard; Hibernate is the implementation.
5. **What does `@Transactional` do?** Runs the method in a database transaction: all changes commit together, or all roll back if an exception is thrown.
6. **Why use DTOs instead of returning the entity?** So the API shape is independent of the database model, and clients cannot send or see fields you do not intend.
7. **How did you validate requests?** Bean Validation annotations on `EmployeeRequest` plus `@Valid` in the controller.
8. **How did you handle exceptions?** One `@RestControllerAdvice` class maps exceptions to status codes and a consistent JSON body.
9. **POST vs PUT?** POST creates a new resource; PUT replaces/updates an existing one at a known id.
10. **Which status codes does the API use?** 200, 201, 204, 400, 404, 409, 500, each for the cases in section 4.
11. **How does React call the backend?** With `fetch` in `api.js`. In development Vite proxies `/api` to Spring Boot.
12. **How is a duplicate email prevented?** The service checks first and returns 409. The `unique = true` column constraint in MySQL is the safety net.
13. **How would you improve it?** Pagination, authentication (Spring Security), migrations (Flyway), Docker, and integration tests.

## 8. Before the interview

- Run the project and use every feature once.
- Open each file and explain it out loud without looking.
- Break something on purpose (send an invalid email, request an id that does not exist) and watch what the API returns.
- Be honest about what you know. If you do not know something, say so and explain how you would find out.
