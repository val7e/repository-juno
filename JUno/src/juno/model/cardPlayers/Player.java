package juno.model.cardPlayers;

import juno.model.cardDeck.*;
import java.util.ArrayList;

public class Player {
    private final String nickname;
    private String avatar;
    private String level;
	private int gamesWon;
	private int gamesLost;
	private int expPoints = 1;

    public Player(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
    
    public String getAvatar() {
    	return avatar;
    }
    
    public void setAvatar(String avatar) {
    	this.avatar = avatar;
    }

    public String getLevel() {
		return this.level;
	}
	
	public int getGamesWon() {
		return this.gamesWon;
	}
	
	public int getGamesLost() {
		return this.gamesLost;
	}
	
	public void addGamesWon() {
		this.gamesWon++;
		this.expPoints *= 2;
		setLevel();
	}
	
	public void addGamesLost() {
		gamesLost++;
	}
	
	public int getExpPoints() {
		return this.expPoints;
	}
	/**
	 * This is method is invoked at the end of every game, after 
	 * the counting points.
	 */
	private void setLevel() {
		if (expPoints >= 64) this.level = "Beginner";
		if (expPoints >= 512) this.level = "Intermediate";
		if (expPoints >= 4096) this.level = "Advanced";
	}

}
