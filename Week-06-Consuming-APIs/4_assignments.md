# Week 6 Assignments

1) Add a new service `NutritionService` that calls an external nutrition API (e.g., Edamam or Spoonacular) to fetch calories for an ingredient.  

2) Expose the nutrition info in a new endpoint `/api/nutrition/{ingredient}`.  

3) Add exception handling for failed API calls (return fallback message).  

4) Modify the Thymeleaf UI to show a random joke under the recipe list.  

5) (Stretch) Use WebClient in a **non-blocking** way (`Mono`/`Flux`) and log the response asynchronously.
