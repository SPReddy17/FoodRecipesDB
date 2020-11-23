package com.codingwithmitch.foodrecipes.util;

import android.arch.lifecycle.LiveData;

import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class LiveDataCallAdapterFactory extends CallAdapter.Factory {
    /*
    this method performs a number of checks and then returns the rsponse type for the retrofit requests
    @bodytype is the response type. it can be reciperesponse or recipesearchresponse

    check#1 returnType returns LiveData
    check#2 Type LiveData<T> is of ApiResponse.class
    check#3 Make sure ApiResponse is parameterizede. AKA ApiResponse<T> exists.
     */


    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        //check #1
        //make sure the calladapter is returning a type of livedata
        if(CallAdapter.Factory.getRawType(returnType) != LiveData.class){
            return  null;
        }

        //check #2
        // type that LiveData is wrapping
        Type observableType = CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) returnType);

        //check if its of type ApiResponse
        Type rawObservableType = CallAdapter.Factory.getRawType(observableType);
        if(rawObservableType != ApiResponse.class){
            throw  new IllegalArgumentException(" Type must be a defined resource");

        }

        //check #3
        //check if ApiResponse is parameterized AKA : does ApiResponse<T> exists?(must wrap around T)
        // FYI : T is either RecipeResponse or T will be a RecipeSearchResponse
        if(!(observableType instanceof ParameterizedType)){
            throw new IllegalArgumentException("resource must be parameterized");
        }

        Type bodyType = CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) observableType);

        return new LiveDataCallAdapter<Type>(bodyType) ;
    }
}
