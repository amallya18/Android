package com.codepath.project.android.network;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.project.android.adapter.CategoryAdapter;
import com.codepath.project.android.helpers.Constants;
import com.codepath.project.android.helpers.Utils;
import com.codepath.project.android.model.AppUser;
import com.codepath.project.android.model.Category;
import com.codepath.project.android.model.Product;
import com.codepath.project.android.model.ViewType;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.codepath.project.android.helpers.Utils.sortByComparator;

public class ParseHelper {

    public static final String PARSE_MASTER_KEY = "myMasterKey";
    public static final String PARSE_APPLICATION_ID = "myAppId";
    public static final String PARSE_SERVER_URL = "https://productreviewsudhish.herokuapp.com/parse/";
    public static final String PARSE_LOGIN_SUCCESS_SNACKTOAST = "Login Successful !";
    public static final String PARSE_LOGIN_FAILED_SNACKTOAST = "Login Failed !";
    public static final String PARSE_SIGNUP_SUCCESS_SNACKTOAST = "Sign Up Successful !";

    private static List<Product> productList;
    private static List<Category> categoryList;

    public static List<Category> getCategoryList() {
        return categoryList;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public static void newUserSignUp(String name,
                                     String password,
                                     String email) {
        AppUser user = new AppUser();
        user.put("firstName", name);
        user.setPassword(password);
        user.setEmail(email);
        user.setUsername(email);
        //user.put("phone", phoneNumber);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
    }

    /*
        Currently not used
        Fetches data from category table
    */
    public static void createCategoryList(ArrayList<Category> categoryList, CategoryAdapter adapter){
        ParseQuery<Category> query = new ParseQuery<Category>("Category");
        query.findInBackground(new FindCallback<Category>() {
            public void done(List<Category> categories, ParseException e) {
                if (e == null) {
                    System.out.println("success "+categories);
                    categoryList.addAll(categories);
                    adapter.notifyDataSetChanged();
                } else {
                    System.out.println("error "+e);
                }
            }
        });
    }

    /*
        Fetches category from products table
        Stores the product list
    */
    public static void createCategoryListFromProducts(ArrayList<Category> categoryList, CategoryAdapter adapter, TextView tvCategories, ProgressBar pbar){
           ParseQuery<Product> query = ParseQuery.getQuery(Product.class);
           query.findInBackground(new FindCallback<Product>() {
            public void done(List<Product> products, ParseException e) {
                if (e == null) {
                    HashMap<String, Product> categoryMap = new HashMap<String, Product>();
                    ArrayList<Category> categoryListTemp = new ArrayList<Category>();
                    for(Product product:products){
                        if(product.getSubCategory() != null) {
                            if (!categoryMap.containsKey(product.getSubCategory())) {
                                categoryMap.put(product.getSubCategory(), product);
                            }
                        }
                    }
                    System.out.println("success "+categoryMap.size());
                    for (Map.Entry<String, Product> entry : categoryMap.entrySet())
                    {
                        String  categoryName = entry.getKey();
                        String  imageUrl = entry.getValue().getImageUrl();
                        Category category = new Category();
                        category.setImageUrl(imageUrl);
                        category.setName(categoryName);
                        categoryListTemp.add(category);
                    }
                    ParseHelper.categoryList = categoryListTemp;
                    ParseHelper.productList = products;
                    categoryList.addAll(categoryListTemp);
                    adapter.notifyDataSetChanged();
                    tvCategories.setVisibility(View.VISIBLE);
                    pbar.setVisibility(View.GONE);
                } else {
                    System.out.println("error "+e);
                }
            }
        });
    }

    public static ArrayList<String> getSubCategoryList(String category){
        HashMap<String, Integer> subCategoryMap = new HashMap<>();
        ArrayList<String> subcategoryList = new ArrayList<>();
        for(Product product:productList){
            String subCat = product.getSubCategory();
            if((subCat == null)||(!subCat.equals(category))){
                continue;
            }
            String brand = product.getBrand();
            if(brand != null) {
                if(!subCategoryMap.containsKey(brand)){
                    subCategoryMap.put(brand, 1);
                }else{
                    int count = subCategoryMap.get(brand);
                    subCategoryMap.put(brand, ++count);
                }
            }
        }
        Map<String, Integer> sortedMap = Utils.sortByComparator(subCategoryMap, false);
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()){
            subcategoryList.add(entry.getKey());
        }
        return subcategoryList;
    }

    public static ArrayList<Product> getProductsForCategory(String category, String subCategory){
        ArrayList<Product> filteredProductList = new ArrayList<>();
        for(Product product:productList){
            String subCat = product.getBrand();
            String cat = product.getSubCategory();
            if(subCategory.equals(Constants.ALL)){
                if(cat != null){
                    if(cat.equals(category)){
                        filteredProductList.add(product);
                    }
                }
            } else {
                if ((cat != null) && (subCat != null)) {
                    if (subCat.equals(subCategory) && cat.equals(category)) {
                        filteredProductList.add(product);
                    }
                }
            }
        }
        return filteredProductList;
    }
}
