package ratioCalculator2;

/**
 * Created by huijiang on 5/23/14.
 * This program reads in subjects with certain health index categories (ie, gender, weight, diabetes) and lipid levels,
 * calculate ratios between each two lipid levels,
 * separate subjects into groups according to health index categories (ie, female/lean/dia- vs female/obese/dia+),
 * and compare if there is significant difference between lipid ratios of two subject groups.
 * The statistical comparison method is Mann-Whitney U test.
 */

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;

public class RatioCalculator2 {
    public static void main (String[] args) {
        //inform subject number and category number
        Scanner readin = new Scanner(System.in);
        System.out.println("How many subjects?");
        int numberOfSubjects = readin.nextInt();
        System.out.println("How many health index categories?");
        int categoryNumber = readin.nextInt();

        //read from a Tab-separated txt file, header line first
        File txtFile = new File("/Users/hui/IdeaProjects/MSwork/shortRatios.txt");
        Scanner readFile = null;
        try {
            readFile = new Scanner(txtFile);
        }
        catch (FileNotFoundException ex) {
            System.out.println("txt file not found!");
        }
        String headerString = readFile.nextLine();
        String[] header = headerString.split("\t");
        String[] categoryID = new String[categoryNumber];
        System.arraycopy(header, 1, categoryID, 0, categoryNumber);
        int lipidNumber = header.length - 1 - categoryNumber;
        String[] lipidID = new String[lipidNumber];
        System.arraycopy(header, categoryNumber + 1, lipidID, 0, lipidNumber);

        //read contents into Subject[]
        Subject[] subjects = new Subject[numberOfSubjects];
        Subject.numberOfLipids = lipidNumber;
        Subject.numberOfCategories = categoryNumber;
        for (int i = 0; i < numberOfSubjects; i++) {
            subjects[i] = new Subject(readFile.nextLine().split("\t"));
        }

        readFile.close();

        //calculate lipid ratios for each element in subjects
        for (int i = 0; i < numberOfSubjects; i++) {
            subjects[i].calculateLipidRatios();
        }

        //input grouping criteria of health index categories
        System.out.println("Please enter categories for group A.\n");
        String[] groupACategories = new String[categoryNumber];
        for (int i = 0; i < categoryNumber; i++ ) {
            System.out.println(categoryID[i] + ":");
            groupACategories[i] = readin.next();
        }
        System.out.println("Please enter categories for group B.\n");
        String[] groupBCategories = new String[categoryNumber];
        for (int i = 0; i < categoryNumber; i++ ) {
            System.out.println(categoryID[i] + ":");
            groupBCategories[i] = readin.next();
        }

        //initial groupA and groupB
        ArrayList<Subject> groupA = new ArrayList<Subject>();
        ArrayList<Subject> groupB = new ArrayList<Subject>();

        //decide if a subject belongs to groupA or groupB
        String[] subcat = new String[categoryNumber];
        boolean match;
        for (int i = 0; i < numberOfSubjects; i++) {
            subcat = subjects[i].getCategories();
            match = true;
            for (int j = 0; j < categoryNumber; j++) {
                if (!(subcat[j].equals(groupACategories[j]))) {
                    match = false;
                    break;
                }
            }
            if (match) {
                groupA.add(subjects[i]);
            }

            match = true;
            for (int j = 0; j < categoryNumber; j++) {
                if (!(subcat[j].equals(groupBCategories[j]))) {
                    match = false;
                    break;
                }
            }
            if (match) {
                groupB.add(subjects[i]);
            }
        }

        //gather lipids of groupA and groupB
        double[][] groupALipids = new double[groupA.size()][subjects[0].lipidRatios.length];
        for (int i = 0; i < groupA.size(); i++) {
            groupALipids[i] = groupA.get(i).lipidRatios;
        }

        double[][] groupBLipids = new double[groupB.size()][subjects[0].lipidRatios.length];
        for (int i = 0; i < groupB.size(); i++) {
            groupBLipids[i] = groupB.get(i).lipidRatios;
        }

        //reverse 2D array's column and row for Mann-Whitney U test
        double[][] groupALipidsReversed = reverse2dArray(groupALipids);
        double[][] groupBLipidsReversed = reverse2dArray(groupBLipids);

        //calculate p values from Mann-Whitney U test and store p values in an array
        MannWhitneyUTest uTest = new MannWhitneyUTest();
        double[] pValues = new double[subjects[0].lipidRatios.length];
        for (int i = 0; i < pValues.length; i++)
            pValues[i] = uTest.mannWhitneyUTest(groupALipidsReversed[i], groupBLipidsReversed[i]);

        //input a p-value range, and print out the results
        String[] ratioHeader = new String[subjects[0].lipidRatios.length];
        int n = 0;
        for (int i = 0; i < lipidNumber - 1; i++) {
            for (int j = i + 1; j < lipidNumber; j++) {
                ratioHeader[n++] = lipidID[i] + "/" + lipidID[j];
            }
        }
        System.out.println("Please input the max for p values, p <= ?");
        double pVal = readin.nextDouble();
        readin.close();
        for (int i = 0; i < pValues.length; i++) {
            if (pValues[i] <= pVal) {
                System.out.println(ratioHeader[i] + "  p = " + pValues[i]);
            }
        }
    }

    public static double[][] reverse2dArray(double[][] originalArray) {
        double[][] reversedArray = new double[originalArray[0].length][originalArray.length];
        for (int i = 0; i < reversedArray.length; i++) {
            for (int j = 0; j < reversedArray[0].length; j++) {
                reversedArray[i][j] = originalArray[j][i];
            }
        }
        return reversedArray;
    }
}

class Subject {
    protected static int numberOfCategories;
    protected static int numberOfLipids;
    private String subjectID;
    private String[] categories;
    private double[] lipids;
    protected double[] lipidRatios;

    public Subject(int categoryNumber, int lipidsNumber, String[] content) {
        numberOfCategories = categoryNumber;
        numberOfLipids = lipidsNumber;
        subjectID = content[0];
        categories = new String[numberOfCategories];
        for (int i = 0; i < categories.length; i++) {
            categories[i] = content[i + 1];
        }
        lipids = new double[numberOfLipids];
        for (int i = 0; i < lipids.length; i++) {
            lipids[i] = Double.parseDouble(content[i + 1 + numberOfCategories]);
        }
    }

    public Subject(String[] content) {
        subjectID = content[0];
        categories = new String[numberOfCategories];
        for (int i = 0; i < categories.length; i++) {
            categories[i] = content[i + 1];
        }
        lipids = new double[numberOfLipids];
        for (int i = 0; i < lipids.length; i++) {
            lipids[i] = Double.parseDouble(content[i + 1 + numberOfCategories]);
        }
    }

    public String getSubjectID() {
        return subjectID;
    }

    public String[] getCategories() {
        return categories;
    }

    public double[] getLipids() {
        return lipids;
    }

    public int getNumberOfCategories() {
        return numberOfCategories;
    }

    public int getNumberOfLipids() {
        return numberOfLipids;
    }

    public void calculateLipidRatios() {
        int numberOfRatios = numberOfLipids * (numberOfLipids - 1) / 2;
        lipidRatios = new double[numberOfRatios];
        int n = 0;
        for (int i = 0; i < numberOfLipids - 1; i++) {
            for (int j = i + 1; j < numberOfLipids; j++) {
                lipidRatios[n++] = lipids[i] / lipids[j];
            }
        }
    }
}