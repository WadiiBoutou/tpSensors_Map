package com.example.drawer.ui.map;



import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PositionApi {

    @POST("/api/positions")
    Call<Position> savePosition(@Body Position position);

    @GET("/api/positions")
    Call<List<Position>> getAllPositions();
}