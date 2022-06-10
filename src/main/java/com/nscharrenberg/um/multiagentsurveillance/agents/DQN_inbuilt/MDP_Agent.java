package com.nscharrenberg.um.multiagentsurveillance.agents.DQN_inbuilt;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.CalculateDistance;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.ManhattanDistance;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect.Sound;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.Vision.CharacterVision;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.Vision.Geometrics;
import com.rits.cloning.Cloner;
import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class MDP_Agent extends Agent implements Encodable {

    private final int observationSize;
    private final CalculateDistance calculateDistance = new ManhattanDistance();
    private final Geometrics gm = new Geometrics();

    public MDP_Agent(Player player) {
        super(player);
        throw new RuntimeException("Wrong constructor");
    }

    public MDP_Agent(Player player, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(player, mapRepository, gameRepository, playerRepository);


        //TODO CHECK
        int visionLength = Double.valueOf(gameRepository.getDistanceViewing()).intValue();
        CharacterVision characterVision = new CharacterVision(visionLength, Action.UP, null);
        List<Tile> vision = characterVision.getConeVision(new Tile(0, 0));

        this.observationSize = (vision.size()-1) + 12;
    }

    public MDP_Agent(Player player, Area<Tile> knowledge, Queue<Action> plannedMoves, IMapRepository mapRepository, IGameRepository gameRepository, IPlayerRepository playerRepository) {
        super(player, knowledge, plannedMoves, mapRepository, gameRepository, playerRepository);
        throw new RuntimeException("Wrong constructor");
    }

    @Override
    public void execute(Action action) {
        try {
            playerRepository.move(player, action);
        } catch (CollisionException | InvalidTileException | ItemNotOnTileException | ItemAlreadyOnTileException | BoardNotBuildException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Action decide() throws Exception {
        return null;
    }

    @Override
    public double[] toArray() {
        double[] observation = new double[observationSize];

        if(player.getTile() == null)
            return observation;

        observation[player.getDirection().ordinal()] = 1;

        if(!(player instanceof Intruder intruder))
            throw new RuntimeException("MDP is not Intruder");

        double distanceToTarget = intruder.getDistanceToTarget();

        observation[4] = distanceToTarget;

        List<Sound> soundList = player.getSoundEffects();

        Sound closestSound = null;

        if(soundList.size() != 0) {
            closestSound = soundList.get(0);

            for (int x = 1; x < soundList.size();x++) {
                Sound tmpSound = soundList.get(x);
                if(closestSound.effectLevel() > tmpSound.effectLevel())
                    closestSound = tmpSound;
            }
        }

        observation[5] = closestSound == null ? 0 : closestSound.effectLevel();

        if(closestSound != null)
            observation[6 + closestSound.actionDirection().ordinal()] = 1;

        observation[10] = player.getRepresentedSound().getRange();

        observation[11] = soundList.size();


        int i = 12;

        Tile approxTargetTile = intruder.getTarget();

        if(approxTargetTile == null){
            for (int j = i; j < observationSize; j++) {
                observation[j] = Double.MAX_VALUE;
                i = j;
            }
            i++;

        } else {
            int visionLength = Double.valueOf(gameRepository.getDistanceViewing()).intValue();

            TileArea board = mapRepository.getBoard();
            Tile position = player.getTile();

            CharacterVision characterVision = new CharacterVision(visionLength, player.getDirection(), player);
            List<Tile> vision = characterVision.getConeVision(player.getTile());

            boolean validtile = true;
            for(Tile tile : vision){

                if(i == 47){
                    System.out.println("GG");
                }
                if(tile.getX() < 0 || tile.getY() < 0){
                    observation[i++] = -1;
                    continue;
                } else if(tile.getX() > board.width() || tile.getY() > board.height()){
                    observation[i++] = -1;
                    continue;
                } else if(tile.getX() == position.getX() && tile.getY() == position.getY())
                    continue;

                for (Tile it : gm.getIntersectingTiles(position, tile)) {
                    if (characterVision.unobstructedTile(board, it)) {
                        validtile = false;
                        break;
                    }
                }

                if(validtile) {
                    Optional<Tile> tileAddOpt = board.getByCoordinates(tile.getX(), tile.getY());

                    if (tileAddOpt.isEmpty()) {
                        observation[i++] = -1;
                        continue;
                    }
                    Tile tileBoard = tileAddOpt.get();

                    if(tileBoard.isWall()){
                        observation[i++] = -1;
                        continue;
                    }else if(tileBoard.hasGuard()){
                        observation[i++] = -4;
                        continue;
                    } else if(tileBoard.isTeleport()){
                        observation[i++] = -3;
                        continue;
                    } else if(tileBoard.hasIntruder()){
                        observation[i++] = -2;
                        continue;
                    }

                    observation[i++] = calculateDistance.compute(tileBoard, approxTargetTile);


                } else {

                    observation[i++] = 0;
                    validtile = true;
                }
            }
        }

        if(i != observationSize){
            throw new RuntimeException("Observation is not full");
        }

        //TODO CHECK OBSERVATION
        return observation;
    }

    @Override
    public boolean isSkipped() {
        return false;
    }

    @Override
    public INDArray getData() {
        return Nd4j.create(toArray());
    }

    @Override
    public Encodable dup() {
        Cloner cloner = new Cloner();
        return cloner.deepClone(this);
    }

    public int getObservationSize() {
        return observationSize;
    }
}
