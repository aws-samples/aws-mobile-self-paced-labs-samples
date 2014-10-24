package com.amazon.example.snake.game;

public interface GameStateListener {

    public void onGameInitialized();

    public void onGameStarted(GameLevel gameLevel);

    public void onGameFinished(GameLevel gameLevel);

    public void onLevelStarted(GameLevel gameLevel);

    public void onLevelFinished(GameLevel gameLevel);
    
}


