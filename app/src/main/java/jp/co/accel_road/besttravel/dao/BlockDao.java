package jp.co.accel_road.besttravel.dao;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import jp.co.accel_road.besttravel.entity.Block;
import jp.co.accel_road.besttravel.entity.Block_Table;

/**
 * ブロック情報を取得するDAO
 *
 * Created by masato on 2016/04/17.
 */
public class BlockDao {

    /**
     * ブロック情報を取得する
     *
     * @param blockId ブロックID
     * @return 友達
     */
    public Block getBlock(long blockId) {
        Block block = SQLite.select().from(Block.class).where(Block_Table.blockId.eq(blockId)).querySingle();

        return block;
    }

    /**
     * ブロック情報リストを取得する
     *
     * @param myAccountId マイアカウントID
     * @return ブロックのリスト
     */
    public List<Block> getBlockList(long myAccountId) {
        List<Block> blockList = SQLite.select().from(Block.class).where(Block_Table.myAccountId.eq(myAccountId)).queryList();

        return blockList;
    }

    /**
     * 対象アカウントのブロック情報を取得する
     *
     * @param myAccountId マイアカウントID
     * @param accountId アカウントID
     * @return ブロック情報
     */
    public Block getBlockByAccountId(long myAccountId, long accountId) {
        Block block = SQLite.select().from(Block.class).where(Block_Table.myAccountId.eq(myAccountId))
                .and(Block_Table.accountId.eq(accountId)).querySingle();

        return block;
    }


    /**
     * ブロック情報を新規作成する。
     *
     * @param block ブロック
     */
    public void insertBlock(Block block) {
        block.insert();
    }

    /**
     * ブロック情報を更新する。
     *
     * @param block ブロック
     */
    public void updateBlock(Block block) {
        block.update();
    }

    /**
     * ブロック情報を削除する。
     *
     * @param blockId ブロックID
     */
    public void deleteBlock(long blockId) {
        SQLite.delete(Block.class).where(Block_Table.blockId.eq(blockId)).execute();
    }

    /**
     * ブロック情報を全て削除する。
     *
     * @param myAccountId マイアカウントID
     */
    public void deleteAllBlock(long myAccountId) {
        SQLite.delete(Block.class).where(Block_Table.myAccountId.eq(myAccountId)).execute();
    }
}
