package com.nscharrenberg.um.multiagentsurveillance.agents.DQN_inbuilt;

import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;
import org.deeplearning4j.rl4j.util.DataManager;
import org.deeplearning4j.rl4j.util.IDataManager;
import org.nd4j.linalg.learning.config.Adam;

import java.io.IOException;

public class DQN_Main {

    public DQN_Main(String text) throws IOException {

        QLearningConfiguration qConfig = QLearningConfiguration.builder()
                .seed(123L)
                .expRepMaxSize(5000)
                .maxEpochStep(10000)
                .maxStep(30000)
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


//        Board board = new Board();
//
//        LogicGame logicGame = new LogicGame(board, true);
//
//        int[][] arrayCoordinate = createArrayCoordinates();
//
//        MDP_Agent mdpAgent = new MDP_Agent(logicGame, true, arrayCoordinate);
//
        IDataManager dataManager = new DataManager(true);
//
//

//        QLearningDiscreteDense<LogicGame> dqn = new QLearningDiscreteDense<LogicGame>(mdpAgent, conf, qConfig);
//        dqn.addListener(new DataManagerTrainingListener(dataManager));
//
//        System.out.println();
//        dqn.train();
//
//        mdpAgent.close();
//
//        try {
//            dqn.getPolicy().save(text);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
