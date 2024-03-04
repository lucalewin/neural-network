package me.lucraft.neuralnetwork;

import java.util.Arrays;

import me.lucraft.neuralnetwork.Utils;
import me.lucraft.utils.*;
import me.lucraft.utils.parser.*;

/**
 * 
 * @author Luca - Created on 05.07.2018
 * @version 1.2.1
 *
 */
public class NeuralNetwork {
	
    private double[][] output;
    private double[][][] weights;
    private double[][] bias;

    private double[][] errorSignal;
    private double[][] outputDerivative;

    private final int[] NETWORK_LAYER_SIZES;
    private final int INPUT_SIZE;
    private final int OUTPUT_SIZE;
    private final int NETWORK_SIZE;

    public NeuralNetwork(int... NETWORK_LAYER_SIZES) {
        this.NETWORK_LAYER_SIZES = NETWORK_LAYER_SIZES;
        this.INPUT_SIZE = NETWORK_LAYER_SIZES[0];
        this.NETWORK_SIZE = NETWORK_LAYER_SIZES.length;
        this.OUTPUT_SIZE = NETWORK_LAYER_SIZES[NETWORK_SIZE-1];

        this.output = new double[NETWORK_SIZE][];
        this.weights = new double[NETWORK_SIZE][][];
        this.bias = new double[NETWORK_SIZE][];

        this.errorSignal = new double[NETWORK_SIZE][];
        this.outputDerivative = new double[NETWORK_SIZE][];

        for(int i = 0; i < this.NETWORK_SIZE; i++) {
            this.output[i] = new double[NETWORK_LAYER_SIZES[i]];
            this.errorSignal[i] = new double[NETWORK_LAYER_SIZES[i]];
            this.outputDerivative[i] = new double[NETWORK_LAYER_SIZES[i]];

            this.bias[i] = Utils.createRandomArray(NETWORK_LAYER_SIZES[i], -0.5,0.7);

            if(i > 0) weights[i] = Utils.createRandomArray(NETWORK_LAYER_SIZES[i], NETWORK_LAYER_SIZES[i-1], -1, 1);
        }
    }

    public double[] calculate(double... input) {
        if(input.length != this.INPUT_SIZE) return null;
        this.output[0] = input;
        for(int layer = 1; layer < NETWORK_SIZE; layer ++) {
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron ++) {
                double sum = bias[layer][neuron];
                for(int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZES[layer-1]; prevNeuron ++)
                    sum += output[layer-1][prevNeuron] * weights[layer][neuron][prevNeuron];
                output[layer][neuron] = sigmoid(sum);
                outputDerivative[layer][neuron] = output[layer][neuron] * (1 - output[layer][neuron]);
            }
        }
        return output[NETWORK_SIZE-1];
    }

    public void train(TrainSet set, int loops, int batch_size) {
        if(set.INPUT_SIZE != INPUT_SIZE || set.OUTPUT_SIZE != OUTPUT_SIZE) return;
        for(int i = 0; i < loops; i++) {
            TrainSet batch = set.extractBatch(batch_size);
            for(int b = 0; b < batch_size; b++) {
                this.train(batch.getInput(b), batch.getOutput(b), 0.3);
            }
        }
    }

    public double MSE(double[] input, double[] target) {
        if(input.length != INPUT_SIZE || target.length != OUTPUT_SIZE) return 0;
        calculate(input);
        double v = 0;
        for(int i = 0; i < target.length; i++) {
            v += (target[i] - output[NETWORK_SIZE - 1][i]) * (target[i] - output[NETWORK_SIZE - 1][i]);
        }
        return v / (2d * target.length);
    }

    public double MSE(TrainSet set) {
        double v = 0;
        for(int i = 0; i < set.size(); i++) 
            v += MSE(set.getInput(i), set.getOutput(i));
        return v / set.size();
    }

    public void train(double[] input, double[] target, double eta) {
        if(input.length != INPUT_SIZE || target.length != OUTPUT_SIZE) return;
        calculate(input);
        backpropError(target);
        updateWeights(eta);
    }

    public void backpropError(double[] target) {
        for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[NETWORK_SIZE - 1]; neuron++) {
            errorSignal[NETWORK_SIZE - 1][neuron] = (output[NETWORK_SIZE - 1][neuron] - target[neuron]) * outputDerivative[NETWORK_SIZE - 1][neuron];
        }
        for(int layer = NETWORK_SIZE - 2; layer > 0; layer--) {
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++){
                double sum = 0;
                for(int nextNeuron = 0; nextNeuron < NETWORK_LAYER_SIZES[layer + 1]; nextNeuron++) 
                    sum += weights[layer + 1][nextNeuron][neuron] * errorSignal[layer + 1][nextNeuron];
                this.errorSignal[layer][neuron] = sum * outputDerivative[layer][neuron];
            }
        }
    }

    public void updateWeights(double eta) {
        for(int layer = 1; layer < NETWORK_SIZE; layer++) {
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {

                double delta = -eta * errorSignal[layer][neuron];
                bias[layer][neuron] += delta;

                for(int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZES[layer - 1]; prevNeuron++) {
                    weights[layer][neuron][prevNeuron] += delta * output[layer - 1][prevNeuron];
                }
            }
        }
    }

    private double sigmoid( double x) {
        return 1d / (double) (1 + Math.exp(-x));
    }
    
    
    /* ----------------------------------------------------------------------------------------------------------------- */
    
    public void saveNetwork(String fileName) throws Exception {
        Parser parser = new Parser();
        parser.create(fileName);
        Node root = parser.getContent();
        Node neuralNetwork = new Node("Network");
        Node layers = new Node("Layers");
        neuralNetwork.addAttribute(new Attribute("sizes", Arrays.toString(this.NETWORK_LAYER_SIZES)));
        neuralNetwork.addChild(layers);
        root.addChild(neuralNetwork);
        for (int layer = 1; layer < this.NETWORK_SIZE; layer++) {
            Node c = new Node("" + layer);
            layers.addChild(c);
            Node weights = new Node("weights");
            Node biases = new Node("biases");
            c.addChild(weights);
            c.addChild(biases);
            biases.addAttribute("values", Arrays.toString(this.bias[layer]));
            for (int weight = 0; weight < this.weights[layer].length; weight++) 
                weights.addAttribute("" + weight, Arrays.toString(this.weights[layer][weight]));
        }
        parser.close();
    }

    public static NeuralNetwork loadNetwork(String fileName) throws Exception {
        Parser parser = new Parser();
		parser.load(fileName);
		String sizes_ = parser.getValue(new String[] { "Network" }, "sizes");
		int[] sizes = ParserTools.parseIntArray(sizes_);
		NeuralNetwork neuralNetwork = new NeuralNetwork(sizes);
		
		for (int i = 1; i < neuralNetwork.NETWORK_SIZE; i++) {
			String biases = parser.getValue(new String[] { "Network", "Layers", new String(i + ""), "biases" }, "values");
			double[] bias = ParserTools.parseDoubleArray(biases);
			neuralNetwork.bias[i] = bias;
			for(int n = 0; n < neuralNetwork.NETWORK_LAYER_SIZES[i]; n++){
				String current = parser.getValue(new String[] { "Network", "Layers", new String(i + ""), "weights" }, "" + n);
				double[] value = ParserTools.parseDoubleArray(current);
				neuralNetwork.weights[i][n] = value;
			}
		}
		parser.close();
		return neuralNetwork;
    }
    
}
