package jp.co.accel_road.besttravel.service;

import jp.co.accel_road.besttravel.model.BlockListDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * ブロック情報に関する通信処理を行う
 *
 * Created by masato on 2016/04/23.
 */
public interface BlockService {

    /**
     * マイアカウントのブロックアカウントを取得する
     */
    @GET("/block/get_block/{accessToken}")
    public Call<BlockListDto> getBlock(@Path("accessToken") final String accessToken);

    /**
     * アカウントIDのユーザーをブロックする
     */
    @GET("/block/insert_block/{accountId}/{accessToken}")
    public Call<BlockListDto> insertBlock(@Path("accountId") final Long accountId, @Path("accessToken") final String accessToken);

    /**
     * ユーザーIDのユーザーをブロック解除する
     */
    @GET("/block/remove_block/{accountId}/{accessToken}")
    public Call<BlockListDto> removeBlock(@Path("accountId") final Long accountId, @Path("accessToken") final String accessToken);
}
