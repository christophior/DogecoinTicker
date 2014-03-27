package christophior.dogecointicker.dogecointicker;

public class points{
    public String date;
    public double price;
    public double time;

    public points(String d, double p){
        date = d;
        price = p*1000;
        time = generateTimeDouble(d);
    }

    public points(double t, double p){
        price = p*1000;
        time = t;
    }

    public double generateTimeDouble(String d){
        double result = 0;
        String[] splitDate = d.split(":");
        result = Double.parseDouble(splitDate[1]) + (Double.parseDouble(splitDate[2])/60);
        System.out.println("SD1: " + splitDate[1] + " SD2: " + splitDate[2]);
        System.out.println("Double rep: " + result);
        return result;
    }
}