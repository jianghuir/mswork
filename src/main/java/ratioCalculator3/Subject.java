package ratioCalculator3;

/**
 * Created by hui on 5/31/14.
 */
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
