package TP.TP2;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.encodings.variable.Int;
import jmetal.util.JMException;

import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math.*;
import java.util.List;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SensorDeployment extends   Problem {

        public ArrayList<Double> CiblesListe;
        public double RayonDetection = 100;

        public SensorDeployment (String solutionType, Integer numberOfVariables) throws ClassNotFoundException {
            numberOfVariables_      = numberOfVariables*2;
            numberOfObjectives_     = 2;
            numberOfConstraints_    = 0;
            problemName_            = "SensorDeployment";

            CiblesListe = new ArrayList<Double>(); //taille 7 ici
            //this.ReadFile("/Users/siditoure/Desktop/esirem/S9/positionstp2Wahabou.csv");
            this.ReadFile("/Users/siditoure/Desktop/esirem/S9/systeme_intelligent_avance_WA/TP/src/positions.csv");

            if (solutionType.compareTo("Real") == 0) {
                solutionType_ = new RealSolutionType(this);
            } else {
                System.out.println("Error: solution type " + solutionType + " invalid.");
                System.exit(-1);
            }

            lowerLimit_ = new double[numberOfVariables_];
            upperLimit_ = new double[numberOfVariables_];

            // définissez les bornes pour (lowerLimit_ et upperLimit_) pour chaque variable de décision.
            //on le met a -100 et 100 pour notre cas
            for (int i=0; i < numberOfVariables_; i++) {
                if(i%2==0){
                    lowerLimit_[i] = 0; //Xmin
                    upperLimit_[i] = 1000; //Xmax
                }
                else{
                    lowerLimit_[i] = 0; //Ymin
                    upperLimit_[i] = 7000; //Ymax
                }

            }
        }

        public void ReadFile(String filePath){
            String csvFile = filePath;
            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ",";


            try {

                br = new BufferedReader(new FileReader(csvFile));
                while ((line = br.readLine()) != null) {

                    // use comma as separator
                    String[] positions = line.split(cvsSplitBy);
                    CiblesListe.add(Double.parseDouble(positions[0]));
                    CiblesListe.add(Double.parseDouble(positions[1]));

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }



        @Override
        public void evaluate(Solution solution) throws JMException {
            Variable[] decisionVariables = solution.getDecisionVariables(); //on recupère ici la position des capteurs en plusieur chromozome ?

            double [] PositionsCapteurs = new double[numberOfVariables_];

            for (int i=0; i < numberOfVariables_; i++) {
                PositionsCapteurs[i] = decisionVariables[i].getValue();
            }

            //récuperons ici le nombre de capteurs qui couvre chaque cible
            Integer NombreCibles = new Integer(CiblesListe.size()/2);
            Integer [] NombreCapteursAutour = new Integer[NombreCibles];
            for (int i = 0; i < NombreCibles*2; i=i+2) {
                NombreCapteursAutour[i/2] = 0;
                for (int k = 0; k < numberOfVariables_; k=k+2) {
                    double r = Math.pow(PositionsCapteurs[k] - CiblesListe.get(i),2) + Math.pow(PositionsCapteurs[k+1] - CiblesListe.get(i+1),2);
                    if (RayonDetection >= Math.sqrt(r)){
                        NombreCapteursAutour[i/2] +=1;
                    }

                }
            }

            double f1 = 0.0;
            double f2 =NombreCapteursAutour[0];
            // calculez f...
            //voir fonction mathematique sur tp
            for (int indiceCible = 0; indiceCible < NombreCibles; indiceCible++) {
                if(NombreCapteursAutour[indiceCible] != 0){
                    f1+=1;
                    if(NombreCapteursAutour[indiceCible]<f2){
                        f2 = (double) NombreCapteursAutour[indiceCible];
                    }
                }
            }
            System.out.println("fonction nombre cibles entourés: " + f1);
            System.out.println("fonction minimum de capteur: " + f2);
            //// a noter que comme le probleme est a maximiser, un moins devant le f dans solution.setObjective(0,f)
            // car solution.setObjective cherche le maximiser des solutions
            solution.setObjective(0, -f1);
            solution.setObjective(1, -f2);

        }


}
