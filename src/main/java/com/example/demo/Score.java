package com.example.tetris.controller;

import com.example.tetris.entity.Player;
import com.example.tetris.entity.Score;
import com.example.tetris.repository.PlayerRepository;
import com.example.tetris.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    // ① 名前の新規登録（重複チェック機能付き）
    @PostMapping("/players/register")
    public ResponseEntity<?> registerPlayer(@RequestBody Map<String, String> request) {
        String name = request.get("name");

        // 既に同じ名前が存在するかデータベースに問い合わせる
        if (playerRepository.existsByName(name)) {
            // 競合（Conflict）を意味するHTTPステータス409を返す
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Name already taken");
        }

        Player player = new Player();
        player.setName(name);
        playerRepository.save(player);
        return ResponseEntity.ok("Registered successfully");
    }

    // ② スコアの送信（プレイ回数のカウントアップ機能付き）
    @PostMapping("/scores")
    public ResponseEntity<?> submitScore(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        int scoreValue = (int) request.get("score");

        Optional<Player> playerOpt = playerRepository.findByName(name);
        if (!playerOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player not found");
        }

        // プレイヤーの「プレイ回数」を1増やして保存
        Player player = playerOpt.get();
        player.setPlayCount(player.getPlayCount() + 1);
        playerRepository.save(player);

        // 新しいスコアをプレイヤーに紐付けて保存
        Score score = new Score();
        score.setPlayer(player);
        score.setScore(scoreValue);
        scoreRepository.save(score);

        return ResponseEntity.ok("Score saved");
    }

    // ③ 全体ランキングの取得
    @GetMapping("/scores")
    public ResponseEntity<List<Map<String, Object>>> getRanking() {
        List<Score> topScores = scoreRepository.findTop10ByOrderByScoreDesc();

        // Entityそのままではなく、Unityが読みやすい形（名前とスコアだけ）に変換して返す
        List<Map<String, Object>> response = topScores.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", s.getPlayer().getName());
            map.put("score", s.getScore());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ④ 個人の戦績（STATS）の取得
    @GetMapping("/players/{name}/stats")
    public ResponseEntity<?> getPlayerStats(@PathVariable String name) {
        Optional<Player> playerOpt = playerRepository.findByName(name);
        if (!playerOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Player not found");
        }

        Player player = playerOpt.get();
        List<Score> allScores = scoreRepository.findByPlayerIdOrderByScoreDesc(player.getId());

        // 1回もプレイしていない場合はハイスコアを0にする
        int highScore = allScores.isEmpty() ? 0 : allScores.get(0).getScore();

        Map<String, Object> stats = new HashMap<>();
        stats.put("name", player.getName());
        stats.put("playCount", player.getPlayCount());
        stats.put("highScore", highScore);

        // 直近のスコア履歴を最大5件まで取得する
        List<Integer> history = allScores.stream()
            .map(Score::getScore)
            .limit(5)
            .collect(Collectors.toList());
        stats.put("history", history);

        return ResponseEntity.ok(stats);
    }
}