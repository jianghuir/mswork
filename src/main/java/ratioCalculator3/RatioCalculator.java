package ratioCalculator3;

/**
 * Created by hui on 5/26/14.
 * * This program reads in subjects with certain health index categories (ie, gender, weight, diabetes) and lipid levels,
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

public class RatioCalculator {
    static int numberOfSubjects;
    static int categoryNumber;
    static int lipidNumber;
    static String[] header;
    static String[] categoryID;
    static String[] lipidID;
    static Subject[] subjects;

    public static void main(String[] args) {
        //input subject number and category number
        inputInitialNumbers();


        //read from a Tab-separated txt file, header line first
        readFromFile();


        //calculate lipid ratios for each element in subjects
        for (Subject sub : subjects) {
            sub.calculateLipidRatios();
        }

        //input grouping criteria of health index categories
        System.out.println("Please enter categories for group A.");
        String[] groupACategories = getGroupCriteria();
        System.out.println("Please enter categories for group B.");
        String[] groupBCategories = getGroupCriteria();


        //gather subjects into groupA and groupB
        ArrayList<Subject> groupA = new ArrayList<Subject>();
        groupA = getGroupSubjects(groupACategories);
        ArrayList<Subject> groupB = new ArrayList<Subject>();
        groupB = getGroupSubjects(groupBCategories);


        //gather lipids of groupA and groupB
        double[][] groupALipids = new double[groupA.size()][subjects[0].lipidRatios.length];
        for (int i = 0; i < groupA.size(); i++) {
            groupALipids[i] = groupA.get(i).lipidRatios;
        }

        double[][] groupBLipids = new double[groupB.size()][subjects[0].lipidRatios.length];
        for (int i = 0; i < groupB.size(); i++) {
            groupBLipids[i] = groupB.get(i).lipidRatios;
        }

        //transpose 2D array's column and row for Mann-Whitney U test
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
        Scanner readin = new Scanner(System.in);
        double pVal = readin.nextDouble();
        readin.close();
        for (int i = 0; i < pValues.length; i++) {
            if (pValues[i] <= pVal) {
                System.out.println(ratioHeader[i] + "  p = " + pValues[i]);
            }
        }
    }

    public static void inputInitialNumbers() {
        Scanner readin = new Scanner(System.in);
        System.out.println("How many subjects?");
        numberOfSubjects = readin.nextInt();
        System.out.println("How many health index categories?");
        categoryNumber = readin.nextInt();
    }

    public static void readFromFile() {
        File txtFile = new File("/Users/hui/IdeaProjects/MSwork/shortRatios.txt");
        Scanner readFile = null;
        try {
            readFile = new Scanner(txtFile);
        } catch (FileNotFoundException ex) {
            System.out.println("txt file not found!");
        }
        String headerString = readFile.nextLine();
        header = headerString.split("\t");
        categoryID = new String[categoryNumber];
        System.arraycopy(header, 1, categoryID, 0, categoryNumber);
        lipidNumber = header.length - 1 - categoryNumber;
        lipidID = new String[lipidNumber];
        System.arraycopy(header, categoryNumber + 1, lipidID, 0, lipidNumber);

        //read contents into Subject[]
        subjects = new Subject[numberOfSubjects];
        Subject.numberOfLipids = lipidNumber;
        Subject.numberOfCategories = categoryNumber;
        for (int i = 0; i < numberOfSubjects; i++) {
            subjects[i] = new Subject(readFile.nextLine().split("\t"));
        }

        readFile.close();
    }

    public static String[] getGroupCriteria() {
        Scanner readin = new Scanner(System.in);
        String[] groupCategories = new String[categoryNumber];
        for (int i = 0; i < categoryNumber; i++) {
            System.out.println(categoryID[i] + ":");
            groupCategories[i] = readin.next();
        }

        return groupCategories;
    }

    public static ArrayList<Subject> getGroupSubjects(String[] criteria) {
        String[] subcat = new String[categoryNumber];
        ArrayList<Subject> group = new ArrayList<Subject>();
        boolean match;
        for (int i = 0; i < numberOfSubjects; i++) {
            subcat = subjects[i].getCategories();
            match = true;
            for (int j = 0; j < categoryNumber; j++) {
                if (!(subcat[j].equals(criteria[j]))) {
                    match = false;
                    break;
                }
            }
            if (match) {
                group.add(subjects[i]);
            }
        }
        return group;
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
