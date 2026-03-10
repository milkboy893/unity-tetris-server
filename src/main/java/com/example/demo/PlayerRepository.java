package com.example.tetris.repository;
import com.example.tetris.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByName(String name);
    boolean existsByName(String name); // 名前がすでに使われているか判定する
}