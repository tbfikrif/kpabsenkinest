package id.kpunikom.absensihadir.control;

import java.util.ArrayList;

import id.kpunikom.absensihadir.model.Item;
import retrofit2.Call;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("getListSudahAbsen.php")
    Call<ArrayList<Item>> getListSudahAbsen();

    @POST("getListBelumAbsen.php")
    Call<ArrayList<Item>> getListBelumAbsen();
}
