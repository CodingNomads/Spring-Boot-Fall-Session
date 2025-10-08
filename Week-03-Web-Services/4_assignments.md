# Week 3 Assignments

1) Extend RecipeController with a search endpoint:  
   - `GET /api/recipes/search?keyword=pancake` → returns recipes by name containing keyword.

2) Add an endpoint to delete all ingredients from a given recipe.  

3) Add validation: recipes must have a non-empty name. Return 400 Bad Request if invalid.  

4) Add pagination to the GET all recipes endpoint using `Pageable`.  

5) (Stretch) Create a DTO (`RecipeDTO`) that only returns recipe `id` and `name` for listing endpoints.
