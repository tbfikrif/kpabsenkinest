package id.kpunikom.absensihadir.control;

import java.util.ArrayList;

import id.kpunikom.absensihadir.model.Item;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface

ApiInterface {

    @POST("getListSudahAbsen.php")
    Call<ArrayList<Item>> getListSudahAbsen();

    @POST("getListBelumAbsen.php")
    Call<ArrayList<Item>> getListBelumAbsen();

    @FormUrlEncoded
    @POST("postHadir.php")
    Call<ArrayList<Item>> postHadir(@Field("id_anggota") String id_anggota, @Field("jam_masuk") String jam_masuk);
}
