package jp.co.accel_road.besttravel.service;

import java.util.List;

import jp.co.accel_road.besttravel.model.DestinationDto;
import jp.co.accel_road.besttravel.model.DestinationSendFileDto;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * 目的地情報に関する通信処理を行う
 *
 * Created by masato on 2016/04/23.
 */
public interface DestinationService {

    /**
     * 目的地の情報を登録する
     */
    @POST("/destination/insert_destination/{accessToken}")
    public Call<DestinationDto> insertDestination(@Body DestinationDto destinationDto, @Path("accessToken") final String accessToken);

    /**
     * 目的地の情報を更新する
     */
    @POST("/destination/update_destination/{accessToken}")
    public Call<DestinationDto> updateDestination(@Body DestinationDto destinationDto, @Path("accessToken") final String accessToken);

    /**
     * 目的地の情報を削除する
     */
    @GET("/destination/delete_destination/{destinationId}/{accessToken}")
    public Call<DestinationDto> deleteDestination(@Path("destinationId") final Long destinationId, @Path("accessToken") final String accessToken);

    /**
     * 目的地のアルバム画像を追加する
     */
    @Multipart
    @POST("/route/insert_destination_album_image/{destinationId}/{accessToken}")
    public Call<DestinationSendFileDto> insertDestinationAlbumImage(@Path("destinationId") final Long destinationId, @Part("description") RequestBody description,
                                                                    @Part List<MultipartBody.Part> imageFileList,
                                                                    @Path("accessToken") final String accessToken);
}
