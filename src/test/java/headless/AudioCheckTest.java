package headless;

import com.google.gson.Gson;
import com.nscharrenberg.um.multiagentsurveillance.agents.AudioAgent.AudioAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect.Sound;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.DistanceEffects;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.GameConfigurationRecorder;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json.AgentJSON;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json.Coordinates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.RecordHelper.GAME_ID;


public class AudioCheckTest {

    private boolean FLAG = false;

    @Test
    void testNavigationByAudio() throws Exception {
        Factory.init();
        new MapImporter().load("src/test/resources/maps/testAudioMap.txt");
        new GameConfigurationRecorder().setUpConfFiles();

        Optional<Tile> optTileX = Factory.getMapRepository().getBoard().getByCoordinates(2, 2);
        Optional<Tile> optTileY = Factory.getMapRepository().getBoard().getByCoordinates(8, 2);
        if(optTileX.isPresent() && optTileY.isPresent()){
            Tile tile = optTileX.get();
            Agent audioAgent = new AudioAgent(new Guard(tile, Action.UP));
            tile.getItems().add(audioAgent.getPlayer());

            tile = optTileY.get();
            Agent stayedAgent = new AudioAgent(new Guard(tile, Action.UP));
            tile.getItems().add(stayedAgent.getPlayer());

            stayedAgent.getPlayer().setRepresentedSoundRange(12);
            audioAgent.getPlayer().setAgent(audioAgent);

            Factory.getPlayerRepository().getAgents().addAll(Arrays.asList(audioAgent, stayedAgent));

            gameLoop(audioAgent);
        } else {
            throw new RuntimeException("Tile not Found test");
        }
        Assertions.assertTrue(FLAG);
    }

    private void gameLoop(Agent agent) {
        IPlayerRepository playerRepository = Factory.getPlayerRepository();
        List<Agent> agents = playerRepository.getAgents();

        Factory.getGameRepository().setRunning(true);
        playerRepository.getStopWatch().start();

        List<List<AgentJSON>> data = createJsonAgentList(agents, playerRepository);

        Thread thread = new Thread(() -> {
            while (Factory.getGameRepository().isRunning()) {
                Factory.getPlayerRepository().calculateExplorationPercentage();
            }
        });
        thread.setDaemon(true);
        thread.start();

        int moveCount = 1;
        int agentId = 0;

        while (Factory.getGameRepository().isRunning()) {

            DistanceEffects.areaEffects(agent, agents);
            List<Sound> soundList = agent.getPlayer().getSoundEffects();

            if(soundList.size() == 0)
                throw new RuntimeException("Sound is Empty");

            Sound sound = soundList.get(0);
            if(sound.effectLevel() >= 91.5) {
                FLAG = true;
                break;
            }

            agent.execute(sound.actionDirection());


            List<AgentJSON> listAgentJSON = data.get(agentId);
            listAgentJSON.add(new AgentJSON(moveCount, 0, 0,
                    new Coordinates(agent.getPlayer().getTile().getX(), agent.getPlayer().getTile().getY()),
                    playerRepository.getExplorationPercentage(), playerRepository.calculateAgentExplorationRate(agent)));


            moveCount++;
        }


        writeJsonData(data);
    }

    private List<List<AgentJSON>> createJsonAgentList(List<Agent> agents, IPlayerRepository playerRepository) {
        List<List<AgentJSON>> data = new ArrayList<>();

        for (int i = 0; i < agents.size(); i++) {
            data.add(new ArrayList<>());
            data.get(i).add(new AgentJSON(0, 0, 0,
                    new Coordinates(agents.get(i).getPlayer().getTile().getX(), agents.get(i).getPlayer().getTile().getY()),
                    playerRepository.getExplorationPercentage(), playerRepository.calculateAgentExplorationRate(agents.get(i))));
        }

        return data;
    }

    private void writeJsonData(List<List<AgentJSON>> data){
        String directoryPath = System.getProperty("user.dir") + "\\DataRecorder\\Game#" + GAME_ID + "\\Agents";
        File agents = new File(directoryPath);
        agents.mkdir();

        Gson gson = new Gson();
        //Write the file in JSON format
        for (int i = 0; i < data.size(); i++) {
            try (FileWriter writer = new FileWriter(directoryPath + "\\Agent#" + i + ".json")) {
                gson.toJson(data.get(i), writer);
                System.out.println("Successfully created the Agent#" + i + ".json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
