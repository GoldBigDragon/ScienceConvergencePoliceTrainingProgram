package goldbigdragon.github.io.function.criminalstatisticsanalysis.calculator.object;

public class SectorObject {
    public int sector;
    public double startLati;
    public double startLongi;
    public double endLati;
    public double endLongi;

    public SectorObject(int sector, double startLati, double startLongi, double endLati, double endLongi){
        this.sector = sector;
        this.startLati = startLati;
        this.startLongi = startLongi;
        this.endLati = endLati;
        this.endLongi = endLongi;
    }

    public SectorObject(SectorObject original){
        this.sector = original.sector;
        this.startLati = original.startLati;
        this.startLongi = original.startLongi;
        this.endLati = original.endLati;
        this.endLongi = original.endLongi;
    }
}
