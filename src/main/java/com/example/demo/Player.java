package com.example.tetris.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique = true で同じ名前の登録をデータベースレベルでブロックする
    @Column(unique = true, nullable = false)
    private String name;

    // 個人の累計プレイ回数を保存するカラム
    @Column(name = "play_count")
    private int playCount = 0;

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPlayCount() { return playCount; }
    public void setPlayCount(int playCount) { this.playCount = playCount; }
}