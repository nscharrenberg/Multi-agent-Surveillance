package headless;

import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.ConvLayer;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.Filter;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.CNN.Kernel;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.DQN_Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.DenseLayer;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.FCN.Neuron;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.neuralNetwork.Network;
import com.nscharrenberg.um.multiagentsurveillance.agents.DQN.utils.NetworkUtils;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class DQNNetworkFileTest {
    @DisplayName("Read & Write DQN Network Data")
    @Test
    void readAndWriteNetwork() throws Exception {
        List<String> networks = new ArrayList<>();
        List<Network> currentNetworks = new ArrayList<>();

        // WRITING
        Factory.init();
        Factory.getGameRepository().startGame();

        Factory.getGameRepository().setRunning(true);

        int iterations = 0;

        while (iterations < 10) {
            iterations++;
            int agentId = 0;

            for (Agent agent : Factory.getPlayerRepository().getAgents()) {
                int oldX = agent.getPlayer().getTile().getX();
                int oldY = agent.getPlayer().getTile().getY();
                Action move = agent.decide();
                agent.execute(move);
                System.out.println("Agent " + agentId
                        + " going from (" + oldX + ", " + oldY + ") to move "
                        + move + " to (" + agent.getPlayer().getTile().getX() + ", "
                        + agent.getPlayer().getTile().getY() + ")");
                agentId++;
            }

            Factory.getMapRepository().checkMarkers();
        }

        for (Agent agent : Factory.getPlayerRepository().getAgents()) {
            if (agent instanceof DQN_Agent dqnAgent) {
                networks.add(NetworkUtils.networkToJson(dqnAgent.getNetwork()));
                currentNetworks.add(dqnAgent.getNetwork());
            }
        }

        // READING
        for (int i = 0; i < networks.size(); i++) {
            String json = networks.get(i);
            Network originalNetwork = currentNetworks.get(i);

            Network importedNetwork = NetworkUtils.jsonToNetwork(json);

            Assertions.assertEquals(originalNetwork.getC1Filters(), importedNetwork.getC1Filters());
            Assertions.assertEquals(originalNetwork.getC2Filters(), importedNetwork.getC2Filters());
            Assertions.assertEquals(originalNetwork.getC3Filters(), importedNetwork.getC3Filters());
            Assertions.assertEquals(originalNetwork.getConv3Length(), importedNetwork.getConv3Length());
            Assertions.assertArrayEquals(originalNetwork.getNetworkOutput(), importedNetwork.getNetworkOutput());

            Assertions.assertEquals(originalNetwork.getActivationLayer().getNumInputs(), importedNetwork.getActivationLayer().getNumInputs());
            Assertions.assertArrayEquals(originalNetwork.getActivationLayer().getInputs(), importedNetwork.getActivationLayer().getInputs());
            Assertions.assertArrayEquals(originalNetwork.getActivationLayer().getOutputs(), importedNetwork.getActivationLayer().getOutputs());

            Assertions.assertEquals(originalNetwork.getDenseLayers().length, importedNetwork.getDenseLayers().length);
            Assertions.assertEquals(originalNetwork.getConvLayers().length, importedNetwork.getConvLayers().length);

            for (int k = 0; k < originalNetwork.getDenseLayers().length; k++) {
                DenseLayer original = originalNetwork.getDenseLayers()[k];
                DenseLayer imported = importedNetwork.getDenseLayers()[k];

                Assertions.assertArrayEquals(original.getInputs(), imported.getInputs());
                Assertions.assertArrayEquals(original.getOutputs(), imported.getOutputs());
                Assertions.assertEquals(original.getNumInputs(), imported.getNumInputs());
                Assertions.assertEquals(original.getNumNeurons(), imported.getNumNeurons());

                for (int m = 0; m < original.getNeurons().length; m++) {
                    Neuron originalNeuron = original.getNeurons()[m];
                    Neuron importedNeuron = imported.getNeurons()[m];
                    Assertions.assertArrayEquals(originalNeuron.getWeights(), importedNeuron.getWeights());
                    Assertions.assertEquals(originalNeuron.getBias(), importedNeuron.getBias());
                    Assertions.assertEquals(originalNeuron.getLearningRate(), importedNeuron.getLearningRate());
                    Assertions.assertEquals(originalNeuron.getNumInputs(), importedNeuron.getNumInputs());
                }
            }

            for (int k = 0; k < originalNetwork.getConvLayers().length; k++) {
                ConvLayer original = originalNetwork.getConvLayers()[k];
                ConvLayer imported = importedNetwork.getConvLayers()[k];

                Assertions.assertEquals(original.getInputLength(), imported.getInputLength());
                Assertions.assertEquals(original.getNumFilters(), imported.getNumFilters());
                Assertions.assertEquals(original.getChannels(), imported.getChannels());
                Assertions.assertEquals(original.getKernelSize(), imported.getKernelSize());
                Assertions.assertArrayEquals(original.getOutput(), imported.getOutput());

                for (int m = 0; m < original.getFilters().length; m++) {
                    Filter originalFilter = original.getFilters()[m];
                    Filter importedFilter = imported.getFilters()[m];

                    Assertions.assertEquals(originalFilter.getChannels(), importedFilter.getChannels());
                    Assertions.assertEquals(originalFilter.getKernelSize(), importedFilter.getKernelSize());
                    Assertions.assertArrayEquals(originalFilter.getInput(), importedFilter.getInput());
                    Assertions.assertArrayEquals(originalFilter.getBias(), importedFilter.getBias());
                    Assertions.assertEquals(originalFilter.getInputLength(), importedFilter.getInputLength());
                    Assertions.assertEquals(originalFilter.getSize(), importedFilter.getSize());
                    Assertions.assertEquals(originalFilter.getLearningRate(), importedFilter.getLearningRate());

                    for (int p = 0; p < originalFilter.getKernels().length; p++) {
                        Kernel originalKernel = originalFilter.getKernels()[p];
                        Kernel importedKernel = importedFilter.getKernels()[p];

                        Assertions.assertEquals(originalKernel.getSize(), importedKernel.getSize());
                        Assertions.assertArrayEquals(originalKernel.getWeights(), importedKernel.getWeights());
                    }
                }
            }
        }
    }
}
