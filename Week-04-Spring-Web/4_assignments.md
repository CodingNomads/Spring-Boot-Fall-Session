# Week 4 Assignments

1) Add a new exception `IngredientNotFoundException` and handle it in `GlobalExceptionHandler`.  

2) Update `IngredientController` to use `ResponseEntity` for all endpoints.  

3) Add validation: return 400 Bad Request if an ingredient has an empty name.  

4) Extend `GlobalExceptionHandler` to return different error messages for validation errors.  

5) (Stretch) Add logging (`LoggerFactory.getLogger`) in `GlobalExceptionHandler` to log all exceptions.
