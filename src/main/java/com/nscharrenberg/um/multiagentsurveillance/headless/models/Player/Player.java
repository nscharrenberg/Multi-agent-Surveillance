package com.nscharrenberg.um.multiagentsurveillance.headless.models.Player;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Collision;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect.Sound;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect.SoundEffect;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect.ISoundEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class Player extends Collision {
    private final String id;
    private Action direction;
    private double speed;
    private Area<Tile> vision;
    private Agent agent;
    private final List<Sound> soundEffects;
    private ISoundEffect representedSound;

    // TODO: Keep track of the state the player is in (moving, standing still, climbing, on_target)

    public Player(Tile tile, Action direction, double speed) {
        super(tile);
        this.id = UUID.randomUUID().toString();
        this.direction = direction;
        this.speed = speed;
        this.agent = null;
        this.soundEffects = new ArrayList<>();
        this.representedSound = new SoundEffect(0);
    }

    public Player(Tile tile, Action direction, double speed, Area<Tile> observation) {
        super(tile);
        this.direction = direction;
        this.speed = speed;
        this.vision = observation;
        this.agent = null;
        this.soundEffects = new ArrayList<>();
        this.representedSound = new SoundEffect(0);
        this.id = UUID.randomUUID().toString();
    }

    public List<Sound> getSoundEffects() {
        return soundEffects;
    }

    public void setRepresentedSoundRange(double range) {
        representedSound.setRange(range);
    }

    public ISoundEffect getRepresentedSound() {
        return representedSound;
    }

    public void setRepresentedSound(ISoundEffect representedSound) {
        this.representedSound = representedSound;
    }

    public Action getDirection() {
        return direction;
    }

    public void setDirection(Action direction) {
        this.direction = direction;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Area<Tile> getVision() {
        return vision;
    }

    public void setVision(Area<Tile> vision) {
        this.vision = vision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Double.compare(player.speed, speed) == 0 && direction == player.direction && Objects.equals(vision, player.vision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, speed, vision);
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getId() {
        return id;
    }
}
