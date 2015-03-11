package edu.stanford.nlp.parser.dvparser;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.util.TwoDimensionalMap;

import org.ejml.simple.SimpleMatrix;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChangeMatrices {
    public static void main( String args[] ) throws IOException {
        String modelPath = null;
        String matrixDir = null;
        String outPath = null;

        for (int argIndex = 0; argIndex < args.length; ) {
          if (args[argIndex].equalsIgnoreCase("-model")) {
            modelPath = args[argIndex + 1];
            argIndex += 2;
          } else if (args[argIndex].equalsIgnoreCase("-output")) {
            outPath = args[argIndex + 1];
            argIndex += 2;
          } else if (args[argIndex].equalsIgnoreCase("-matrixes")) {
            matrixDir = args[argIndex + 1];
            argIndex += 2;
          } else {
            System.err.println("Unknown argument " + args[argIndex]);
          }
        }
        
        LexicalizedParser parser = LexicalizedParser.loadModel(modelPath);
        DVModel model = DVParser.getModelFromLexicalizedParser(parser);
        
        String binaryWDir = matrixDir + File.separator + "binaryW";
        
        for (Iterator<TwoDimensionalMap.Entry<String, String, SimpleMatrix>> it = model.binaryTransform.iterator(); it.hasNext(); ) {
            TwoDimensionalMap.Entry<String, String, SimpleMatrix> entry = it.next();
            String filename = binaryWDir + File.separator + entry.getFirstKey() + "_" + entry.getSecondKey() + ".txt";
            SimpleMatrix newMatrix = SimpleMatrix.loadCSV(filename);
            
            model.binaryTransform.put(entry.getFirstKey(), entry.getSecondKey(), newMatrix);
            //entry.setValue(newMatrix);
        }

        LexicalizedParser newParser = LexicalizedParser.copyLexicalizedParser(parser);
        DVModelReranker reranker = new DVModelReranker(model);
        newParser.reranker = reranker;
        newParser.saveParserToSerialized(outPath);

    }
    
}
