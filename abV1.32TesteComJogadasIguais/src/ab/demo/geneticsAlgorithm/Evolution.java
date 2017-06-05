package ab.demo.geneticsAlgorithm;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
public class Evolution{
    private int bodyLength=1;
    private int sizeOfForcesAnglesAndSpecial;
    private ArrayList<Double> bodyForceSelected=new ArrayList<Double>();
    private ArrayList<Double> bodyAngleSelected=new ArrayList<Double>();
    private ArrayList<Double> bodySpecialSelected=new ArrayList<Double>();
    private int generation=0;
    ArrayList<Body> bodiesUnused ;   
    ArrayList<Body> bodiesUsed ;   
    public Evolution(File previousGenerationFile,int sizeOfForcesAnglesAndSpecial){
        bodiesUsed= new ArrayList<Body>();
        bodiesUnused= new ArrayList<Body>();
        try{
            FileReader arq = new FileReader(previousGenerationFile);
            BufferedReader lerArq = new BufferedReader(arq);
            String line=null;
            int numberLines=0;

                line= lerArq.readLine();
                line= lerArq.readLine();
                if(line ==null) 
                    bodiesUnused=bodiesGenerateDefault();
                else{
                    line=line.replace(",",".");
                    String forcesAndAnglesAndSpecialsAndScoreAndIdAndGeneration[]= line.split("@");
                    Body body=new Body();
                    
                    ArrayList<Double> angles=new ArrayList<Double>();
                    ArrayList<Double> forces= new ArrayList<Double>();
                    ArrayList<Double> special= new ArrayList<Double>();
                    for(int j=0;j<5;j++){
                        System.out.print(forcesAndAnglesAndSpecialsAndScoreAndIdAndGeneration[j*3]+"@");
                        forces.add(Double.valueOf(forcesAndAnglesAndSpecialsAndScoreAndIdAndGeneration[j*3]));
                        System.out.print(forcesAndAnglesAndSpecialsAndScoreAndIdAndGeneration[(j*3)+1]+"@");
                        angles.add(Double.valueOf(forcesAndAnglesAndSpecialsAndScoreAndIdAndGeneration[(j*3)+1]));
                        System.out.print(forcesAndAnglesAndSpecialsAndScoreAndIdAndGeneration[(j*3)+2]+"@");
                        special.add(Double.valueOf(forcesAndAnglesAndSpecialsAndScoreAndIdAndGeneration[(j*3)+2]));
                    }
                    body.setForces(forces);
                    body.setAngles(angles);
                    body.setTimesToActivateSpecial(special);
                    body.setScore(Double.valueOf(forcesAndAnglesAndSpecialsAndScoreAndIdAndGeneration[15]));
                    System.out.print(forcesAndAnglesAndSpecialsAndScoreAndIdAndGeneration[15]+"@");
                    body.setId (Long.valueOf(forcesAndAnglesAndSpecialsAndScoreAndIdAndGeneration[16]));                            
                    System.out.println(forcesAndAnglesAndSpecialsAndScoreAndIdAndGeneration[16]);
                    generation=Integer.valueOf(forcesAndAnglesAndSpecialsAndScoreAndIdAndGeneration[17]);
                    bodiesUsed.add(body);
                }
                
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        this.bodyLength=bodyLength;
    }
    public Evolution(int sizeOfForcesAnglesAndSpecial){
        this.bodyLength=bodyLength;
        bodiesUsed= new ArrayList<Body>();
        this.sizeOfForcesAnglesAndSpecial=sizeOfForcesAnglesAndSpecial;        
        bodiesUnused=bodiesGenerateDefault();
    }
 public void restoreForceAngleAndSpecial(Double force,Double angle, Double special){
        bodyForceSelected.add(0,force);
        bodyAngleSelected.add(0,angle);
        bodySpecialSelected.add(0,special);
    }
    private ArrayList<Body> bodiesGenerateDefault(){
        ArrayList<Body> bodies= new ArrayList<Body>();
        for(int i=0;i<bodyLength;i++){
            Body body=new Body();
            ArrayList<Double> forces= new ArrayList<Double>();
            ArrayList<Double> angles= new ArrayList<Double>();
            ArrayList<Double> special = new ArrayList<Double>();
            forces.add(25.722411);
            angles.add(42.277592);
            special.add(1997.307503);
            
            forces.add(37.074208);
            angles.add(25.240688);
            special.add(538.192642);

            forces.add(37.126539);
            angles.add(21.507873);
            special.add(849.014255);

            forces.add(29.007013);
            angles.add(76.845424);
            special.add(2442.591532);

            forces.add(31.620577);
            angles.add(31.247787);
            special.add(1547.914065);
            body.setId(4564);
            
            body.setForces(forces);
            body.setAngles(angles);
            body.setTimesToActivateSpecial(special);
            bodies.add(body);
        }
        
        return bodies;
    }
    public void setScore(int score){
        bodiesUsed.get(bodiesUsed.size()-1).setScore(score);
    }
    public long getIdBody(){
        return bodiesUsed.get(bodiesUsed.size()-1).getId();
    }

    public void createNextGeneration(){
        Body body=new Body();
        body.setForces(bodiesUsed.get(0).getForces());
        body.setTimesToActivateSpecial(bodiesUsed.get(0).getTimesToActivateSpecial());
        body.setAngles(bodiesUsed.get(0).getAngles());
        body.setId(body.getId());
        bodiesUnused.add(body);
        bodiesUsed.clear();
    }

    public double[] getForceAngleAndSpecial(){
        if(bodyAngleSelected.size()!=0){
            System.out.println("nBody of generation: "+ (bodiesUnused.size()+1));
            System.out.println("shot:"+ bodyAngleSelected.size());
        }
        else{
            System.out.println("nBody of generation: "+ bodiesUnused.size());
            System.out.println("shot: 5");
        }
        if(bodyAngleSelected.size()==0 || bodyForceSelected.size()==0 || bodySpecialSelected.size()==0 ) {
            if(bodiesUnused.size()==0){
                System.out.println("Next generation");
                generation++;
                createNextGeneration();
            }
            Body bodySelected=bodiesUnused.get(0);
            bodiesUnused.remove(0);
            bodiesUsed.add(bodySelected);
            bodyForceSelected=bodySelected.getForces();
            bodyAngleSelected=bodySelected.getAngles();
            bodySpecialSelected=bodySelected.getTimesToActivateSpecial();
        }
        double ret[]= new double[3];
        System.out.println("1");
        ret[0]=bodyForceSelected.get(0);
        System.out.println("2");
        ret[1]=bodyAngleSelected.get(0);
        System.out.println("3");
        ret[2]=bodySpecialSelected.get(0);
        bodyForceSelected.remove(0);
        bodyAngleSelected.remove(0);
        bodySpecialSelected.remove(0);
        return ret;        
    }
    public int getGeneration(){
        return generation;
    }
}
