package com.nscharrenberg.um.multiagentsurveillance.agents.DQN_inbuilt;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.Vision.ConeVisionCalculator;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.util.DataManager;
import org.deeplearning4j.rl4j.util.DataManagerTrainingListener;
import org.deeplearning4j.rl4j.util.IDataManager;

import java.io.IOException;
import java.util.List;

public class DQN_Main {

    public DQN_Main(String text) throws IOException {

        QLearningConfiguration qConfig = QLearningConfiguration.builder()
                .seed(1L)
                .maxEpochStep(200)
                .maxStep(500000)
                .updateStart(0)
                .rewardFactor(1.0)
                .gamma(0.99)
                .errorClamp(1.0)
                .batchSize(32)
                .minEpsilon(0.0)
                .epsilonNbStep(1000)
                .expRepMaxSize(5000)
                .doubleDQN(true)
                .build();


//        DQNDenseNetworkConfiguration conf = DQNDenseNetworkConfiguration.builder()
//                .updater(new Adam(0.0025))
//                .numHiddenNodes(60)
//                .numLayers(5)
//                .build();

        DQNPolicy<DeepQN_Agent> p = DQNPolicy.load("src/test/resources/bins/test11.bin");
        IDQN conf = (IDQN) p.getNeuralNet();

        System.out.println(qConfig.toString());
        System.out.println(conf.toString());


        List<Tile> vision = ConeVisionCalculator.getConeVision(new Tile(0,0), 6, Action.UP);

        int observationSize = (vision.size()) + 12;

        MDP_API mdpAgent = new MDP_API(observationSize);

        IDataManager dataManager = new DataManager(true);

        final QLearningDiscreteDense<DeepQN_Agent> dqn = new QLearningDiscreteDense<>(mdpAgent, conf, qConfig);
        dqn.addListener(new DataManagerTrainingListener(dataManager));


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
