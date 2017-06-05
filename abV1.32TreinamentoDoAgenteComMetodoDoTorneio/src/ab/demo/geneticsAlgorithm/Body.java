package ab.demo.geneticsAlgorithm;
import java.util.ArrayList;
public class Body{
    private ArrayList<Double> angles;
    private ArrayList<Double> forces;
    private ArrayList<Double> timesToActivateSpecial;
    private double score;
    private long id;
    public Body(){
        angles= new ArrayList<Double>();
        forces= new ArrayList<Double>();        
        timesToActivateSpecial= new ArrayList<Double>();
        id=Math.round( Math.random()*1000000.0);
    }
    public ArrayList<Double> getAngles(){
        ArrayList<Double> angles= new ArrayList<Double>();
        for(Double d:this.angles)
            angles.add(d);

        return angles;
    }
    public ArrayList<Double> getForces(){
        ArrayList<Double> forces= new ArrayList<Double>();
        for(Double d:this.forces)
            forces.add(d);

        return forces;

    }
    public void setTimesToActivateSpecial( ArrayList<Double> timesToActivateSpecial ){
        this.timesToActivateSpecial=new ArrayList<Double>();
        for(Double d:timesToActivateSpecial){
            this.timesToActivateSpecial.add(d);
        }
    }
    public ArrayList<Double> getTimesToActivateSpecial(){
        ArrayList<Double> timesToActivateSpecial= new ArrayList<Double>();
        for(Double d:this.timesToActivateSpecial)
            timesToActivateSpecial.add(d);

        return timesToActivateSpecial;

    }

    public void setAngles(ArrayList<Double> angles){
        this.angles= new ArrayList<Double>();
        for(Double d:angles)
            this.angles.add(d);
    }
    public void setForces( ArrayList<Double> forces){
        this.forces= new ArrayList<Double>();
        for(Double d:forces)
            this.forces.add(d);
    }
    public double getScore(){
        return score;
    }
    public void setScore(double score){
        this.score=score;
    }
    public long getId(){
        return id;
    }
    public void setId(long id){
        this.id=id;
    }

}
