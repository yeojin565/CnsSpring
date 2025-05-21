package com.example.springjwt.recipe;

import com.example.springjwt.fridge.Fridge;
import com.example.springjwt.fridge.FridgeRepository;
import com.example.springjwt.mypage.LikeRecipe;
import com.example.springjwt.mypage.LikeRecipeRepository;
import com.example.springjwt.User.UserEntity;
import com.example.springjwt.User.UserRepository;
import com.example.springjwt.point.PointActionType;
import com.example.springjwt.point.PointService;
import com.example.springjwt.review.Recipe.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class  RecipeService {
    private final ReviewRepository reviewRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final PointService pointService;
    private final LikeRecipeRepository likeRecipeRepository;
    private final FridgeRepository fridgeRepository;

    // 전체 레시피 조회
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // 공개된 레시피만 정렬해서 가져오기
    public List<RecipeSearchResponseDTO> getAllPublicRecipes(String sort) {
        List<Recipe> recipes;
        // 로그인한 사용자 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByUsername(username);

        switch (sort != null ? sort : "viewCount") {
            case "likes":
                recipes = recipeRepository.findByIsPublicTrueOrderByLikesDesc();
                break;
            case "latest":
                recipes = recipeRepository.findByIsPublicTrueOrderByCreatedAtDesc();
                break;
            case "shortTime":
                recipes = recipeRepository.findByIsPublicTrueOrderByCookingTimeAsc();
                break;
            case "longTime":
                recipes = recipeRepository.findByIsPublicTrueOrderByCookingTimeDesc();
                break;
            case "viewCount":
            default:
                recipes = recipeRepository.findByIsPublicTrueOrderByViewCountDesc();
                break;
        }

        return recipes.stream().map(recipe -> {
            Double avgRatingWrapper = reviewRepository.findAvgRatingByRecipe(recipe.getRecipeId());
            double avgRating = avgRatingWrapper != null ? avgRatingWrapper : 0.0;
            int reviewCount = reviewRepository.countByRecipe(recipe);
            boolean liked = likeRecipeRepository.existsByUserAndRecipe(currentUser, recipe);

            return RecipeSearchResponseDTO.fromEntity(recipe, avgRating, reviewCount, liked);
        }).collect(Collectors.toList());
    }

    // 특정 레시피 조회
    public Recipe getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다: " + id));
        recipe.setViewCount(recipe.getViewCount() + 1); // 조회수 1 증가
        recipeRepository.save(recipe); // 저장
        return recipe;
    }

    // 레시피 생성
    public Recipe createRecipe(RecipeDTO recipeDTO, String username) {
        UserEntity user = userRepository.findByUsername(username);
        Recipe recipe = recipeDTO.toEntity();
        System.out.println("로그인 된 유저 :"+user.getUsername());
        recipe.setUser(user); // 로그인한 유저를 레시피 작성자로 설정
        pointService.addPoint(
                user,
                PointActionType.RECIPE_WRITE,
                1,
                "레시피 작성 포인트 10점 적립"
        );
        return recipeRepository.save(recipe);
    }

    // 레시피 수정
    public Recipe updateRecipe(Long id, RecipeDTO recipeDTO) {
        Recipe existingRecipe = getRecipeById(id);
        existingRecipe.setTitle(recipeDTO.getTitle());
        existingRecipe.setCategory(RecipeCategory.valueOf(recipeDTO.getCategory()));
        existingRecipe.setIngredients(recipeDTO.getIngredients());
        existingRecipe.setAlternativeIngredients(recipeDTO.getAlternativeIngredients());
        existingRecipe.setCookingSteps(recipeDTO.getCookingSteps());
        existingRecipe.setMainImageUrl(recipeDTO.getMainImageUrl());
        existingRecipe.setDifficulty(RecipeDifficulty.valueOf(recipeDTO.getDifficulty()));
        existingRecipe.setCookingTime(recipeDTO.getCookingTime());
        existingRecipe.setServings(recipeDTO.getServings());
        existingRecipe.setPublic(recipeDTO.getIsPublic());
        return recipeRepository.save(existingRecipe);
    }

    // 레시피 삭제
    public void deleteRecipe(Long id) {
        Recipe recipe = getRecipeById(id);
        recipeRepository.delete(recipe);
    }

    // 레시피 검색
    public List<RecipeSearchResponseDTO> searchRecipesByTitle(String title) {
        List<Recipe> recipes;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByUsername(username);
        if (title == null || title.trim().isEmpty()) {
            recipes = recipeRepository.findAll();
        } else {
            recipes = recipeRepository.findByTitleContainingIgnoreCase(title);
        }

        return recipes.stream()
                .map(recipe -> {
                    Double avgRatingWrapper = reviewRepository.findAvgRatingByRecipe(recipe.getRecipeId());
                    double avgRating = avgRatingWrapper != null ? avgRatingWrapper : 0.0;
                    int reviewCount = reviewRepository.countByRecipe(recipe);
                    boolean liked = likeRecipeRepository.existsByUserAndRecipe(currentUser, recipe);

                    return RecipeSearchResponseDTO.fromEntity(recipe, avgRating, reviewCount, liked);
                })
                .collect(Collectors.toList());
    }

    //메인-냉장고 재료 추천 레시피
    public List<RecipeSearchResponseDTO> getRecommendedRecipesByTitleKeywords(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return Collections.emptyList();
        }

        List<Recipe> recipes = recipeRepository.findByIsPublicTrue(); // 공개 레시피 전체 조회

        List<Recipe> filtered = recipes.stream()
                .filter(recipe -> keywords.stream()
                        .anyMatch(keyword -> recipe.getTitle().contains(keyword)))
                .sorted(Comparator.comparingInt(Recipe::getViewCount).reversed())
                .limit(10) // 예: 최대 10개까지만 추천
                .collect(Collectors.toList());

        return filtered.stream()
                .map(recipe -> RecipeSearchResponseDTO.fromEntity(recipe, 0.0, recipe.getLikes(), false))
                .collect(Collectors.toList());
    }

    //메인-냉장고 재료 추천 레시피 그룹
    public List<IngredientRecipeGroup> getGroupedRecommendedRecipesByTitle(List<String> keywords) {
        List<Recipe> allRecipes = recipeRepository.findByIsPublicTrue();

        return keywords.stream()
                .map(keyword -> {
                    List<Recipe> matched = allRecipes.stream()
                            .filter(recipe -> recipe.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                            .sorted(Comparator.comparingInt(Recipe::getViewCount).reversed())
                            .limit(2)
                            .collect(Collectors.toList());

                    List<RecipeSearchResponseDTO> dtos = matched.stream()
                            .map(recipe -> RecipeSearchResponseDTO.fromEntity(recipe, 0.0, recipe.getLikes(), false))
                            .collect(Collectors.toList());

                    return new IngredientRecipeGroup(keyword, dtos);
                })
                .collect(Collectors.toList());
    }

    //예상 사용 재료
    public List<ExpectedIngredientDTO> getExpectedIngredients(Long recipeId, UserEntity user) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("레시피를 찾을 수 없습니다."));

        List<Fridge> fridgeList = fridgeRepository.findByUserIdOrderByUpdatedAtDesc((long) user.getId());

        JSONArray ingredients = new JSONArray(recipe.getIngredients());
        List<ExpectedIngredientDTO> result = new ArrayList<>();

        for (int i = 0; i < ingredients.length(); i++) {
            JSONObject item = ingredients.getJSONObject(i);
            String name = item.optString("name", "").trim();
            String amount = item.optString("amount", "").trim();

            if (!name.isEmpty()) {
                fridgeList.stream()
                        .filter(f -> f.getIngredientName().contains(name))
                        .findFirst()
                        .ifPresent(fridgeItem -> {
                            result.add(new ExpectedIngredientDTO(
                                    name,
                                    amount,
                                    fridgeItem.getQuantity() + " " + fridgeItem.getUnitDetail(),
                                    fridgeItem.getFridgeDate() != null ? fridgeItem.getFridgeDate().toString() : "날짜 없음"
                            ));
                        });
            }
        }

        return result;
    }

}