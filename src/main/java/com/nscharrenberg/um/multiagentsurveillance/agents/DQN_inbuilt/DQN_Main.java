package com.nscharrenberg.um.multiagentsurveillance.agents.DQN_inbuilt;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.Vision.CharacterVision;
import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;
import org.nd4j.linalg.learning.config.Adam;

import java.io.IOException;
import java.util.List;

public class DQN_Main {

    public DQN_Main(String text) throws IOException {

        QLearningConfiguration qConfig = QLearningConfiguration.builder()
                .seed(123L)
                .expRepMaxSize(5000)
                .maxEpochStep(10000)
                .maxStep(10000)
                .batchSize(32)
                .targetDqnUpdateFreq(100)
                .updateStart(10)
                .rewardFactor(1.0)
                .gamma(0.99)
                .errorClamp(1.0)
                .minEpsilon(0.1f)
                .epsilonNbStep(10000)
                .doubleDQN(true)
                .build();


        DQNDenseNetworkConfiguration conf = DQNDenseNetworkConfiguration.builder()
                .updater(new Adam(0.3))
                .numHiddenNodes(32)
                .numLayers(3)
                .build();


        //TODO CHECK
        CharacterVision characterVision = new CharacterVision(5, Action.UP, null);
        List<Tile> vision = characterVision.getConeVision(new Tile(0,0));

        int observationSize = (vision.size()-1) + 12;

        MDP_API mdpAgent = new MDP_API(observationSize);

        //IDataManager dataManager = new DataManager(true);

        final QLearningDiscreteDense<MDP_Agent> dqn = new QLearningDiscreteDense<>(mdpAgent, conf, qConfig);
        //dqn.addListener(new DataManagerTrainingListener(dataManager));

        System.out.println();
        dqn.train();

        mdpAgent.close();

        try {
            dqn.getPolicy().save(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
