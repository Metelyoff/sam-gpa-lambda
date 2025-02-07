package robot_dreams.aws.lambdas;

public class Data {

    private int sat;
    private double gpa;

    public Data() {
        this.sat = 0;
        this.gpa = 0.0;
    }

    public Data(int sat, double gpa) {
        this.sat = sat;
        this.gpa = gpa;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public int getSat() {
        return sat;
    }

    public void setSat(int sat) {
        this.sat = sat;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Data data = (Data) o;
        return sat == data.sat && Double.compare(gpa, data.gpa) == 0;
    }

    @Override
    public int hashCode() {
        int result = sat;
        result = 31 * result + Double.hashCode(gpa);
        return result;
    }

}
