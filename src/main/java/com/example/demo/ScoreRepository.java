package com.example.tetris.repository;
import com.example.tetris.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    // 個人のハイスコア履歴用（プレイヤーIDで絞り込み、スコアの降順で取得）
    List<Score> findByPlayerIdOrderByScoreDesc(Long playerId);

    // 全体ランキング用（無条件でスコアの降順トップ10を取得）
    List<Score> findTop10ByOrderByScoreDesc();
}