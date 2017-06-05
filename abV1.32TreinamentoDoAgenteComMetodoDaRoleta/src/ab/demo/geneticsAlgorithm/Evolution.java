package ab.demo.geneticsAlgorithm;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
public class Evolution{
    private int bodyLength=40;
    private int sizeOfForcesAnglesAndSpecial;
    private ArrayList<Double> bodyForceSelected=new ArrayList<Double>();
    private ArrayList<Double> bodyAngleSelected=new ArrayList<Double>();
    private ArrayList<Double> bodySpecialSelected=new ArrayList<Double>();
    private int generation=0;
    ArrayList<Body> bodiesUnused ;   
    ArrayList<Body> bodiesUsed ;   
    public Evolution(File previousGenerationFile,int sizeOfForcesAnglesAndSpecial){
        this.sizeOfForcesAnglesAndSpecial=sizeOfForcesAnglesAndSpecial;        
        try{
            FileReader arq = new FileReader(previousGenerationFile);
            BufferedReader lerArq = new BufferedReader(arq);
            String line=null;
            int numberLines=0;
            while(true){
                line= lerArq.readLine();
                if(line==null) break;
                numberLines++;
            }
            lerArq.close();
            arq.close();
            if(numberLines>bodyLength){
                System.out.println("Load previus Generation from file===========");
                arq = new FileReader(previousGenerationFile);
                lerArq = new BufferedReader(arq);           
                bodiesUsed= new ArrayList<Body>();
                bodiesUnused= new ArrayList<Body>();
                int readLine=0;
                while(true){
                    line= lerArq.readLine().replace(',','.');
                    if((numberLines-bodyLength-1)==readLine) break;
                    readLine++;
                }             
                for(int i=0;i<bodyLength;i++){
                    line= lerArq.readLine().replace(',','.');
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
                
                System.out.println("============================================");
            }
            else{
                this.bodyLength=bodyLength;
                bodiesUsed= new ArrayList<Body>();
                
                bodiesUnused=bodiesGenerateRandomly();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    public Evolution(int sizeOfForcesAnglesAndSpecial){
        this.bodyLength=bodyLength;
        bodiesUsed= new ArrayList<Body>();
        this.sizeOfForcesAnglesAndSpecial=sizeOfForcesAnglesAndSpecial;        
        bodiesUnused=bodiesGenerateRandomly();
    }
    public  ArrayList<Body> bodiesGenerateRandomly(){
        ArrayList<Body> bodies= new ArrayList<Body>();
        for(int i=0;i<bodyLength;i++){
            Body body=new Body();
            ArrayList<Double> forces= new ArrayList<Double>();
            ArrayList<Double> angles= new ArrayList<Double>();
            ArrayList<Double> special = new ArrayList<Double>();
            for(int j=0;j<sizeOfForcesAnglesAndSpecial;j++){
                forces.add(Math.random()*45.0);
                angles.add(Math.random()*80.0);
                special.add(Math.random()*2500.0);
            }
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
    private Body selectBody(Body body1, Body body2){
        Body  bodySelected=null;
        if(body1.getScore()> body2.getScore()){
            bodySelected=body1;
        }
        else{
            bodySelected=body2;
        }
        return bodySelected;
    } 
    private Body selectBodyInBodiesUsed(){        
        int select=Math.round((float)(Math.random()*(bodiesUsed.size()-1)));
        Body body=bodiesUsed.get(select);
        bodiesUsed.remove(select);
        return body;

    }
    private ArrayList<Body> bodiesSelectedFromRoulette(){
        class BodyInRoulette{
            public int begin;
            public int end;
            public Body body;
        }
        int previousEnd=0;
        ArrayList<BodyInRoulette> roulette=new ArrayList<BodyInRoulette>();
        for(int i=0;i<bodiesUsed.size();i++){
            BodyInRoulette temp=new BodyInRoulette();
            temp.begin=previousEnd;
            temp.end=previousEnd+(int)Math.round(bodiesUsed.get(i).getScore());
            temp.body=bodiesUsed.get(i);
            roulette.add(temp);
            previousEnd=temp.end;
        }
        ArrayList<Body> ret=new ArrayList<Body>();
        while(ret.size()<2){
            int select=Math.round((float)(Math.random()*(previousEnd-1)));
            for(int i=0;i<roulette.size();i++){
                BodyInRoulette temp= roulette.get(i);
                if(select>=temp.begin && select<temp.end){
                    boolean bodyWithinOfRet=false;
                    for(int j=0;j<ret.size();j++){
                        if(temp.body==ret.get(j)){
                            bodyWithinOfRet=true;
                            break;
                        }
                    }
                    if(bodyWithinOfRet==false) ret.add(temp.body);
                    break;
                }
            }
        }
        return ret;
    }
    private ArrayList<Body> bodiesSelectedFromTournament(){
        Body bodyt1=selectBodyInBodiesUsed();
        Body bodyt2=selectBodyInBodiesUsed();
        Body bodyt3=selectBodyInBodiesUsed();
        Body bodyt4=selectBodyInBodiesUsed();
        Body bodySelected1=selectBody(bodyt1, bodyt2);
        Body bodySelected2=selectBody(bodyt3, bodyt4);
        ArrayList<Body> ret=new ArrayList<Body>();
        ret.add(bodySelected1);
        ret.add(bodySelected2);
        bodiesUsed.add(bodyt1);
        bodiesUsed.add(bodyt2);
        bodiesUsed.add(bodyt3);
        bodiesUsed.add(bodyt4);

        return ret;
    }
    public void mutationBody(Body body){
        ArrayList<Double> forcesChromosomes=body.getForces();
        ArrayList<Double> anglesChromosomes=body.getAngles();
        ArrayList<Double> specialChromosomes=body.getTimesToActivateSpecial();
        for(int i=0;i< forcesChromosomes.size();i++){
            if(Math.random()<0.025){
                Double valueForce;
                while(true){
                    valueForce=forcesChromosomes.get(i)+((Math.random()*4.0)-2.0);
                    boolean cond=true;
                    if(valueForce<0.0) cond=false;
                    if(cond) break;                    
                }
                forcesChromosomes.set(i,valueForce);
            }
        }
        for(int i=0;i< anglesChromosomes.size();i++){
            if(Math.random()<0.025){
                Double valueAngle;
                while(true){
                    valueAngle=anglesChromosomes.get(i)+((Math.random()*4.0)-2.0);
                    boolean cond=true;
                    if(valueAngle<0.0) cond=false;
                    if(cond) break;

                }
                anglesChromosomes.set(i,valueAngle);
            }
        }
        for(int i=0;i< specialChromosomes.size();i++){
            if(Math.random()<0.025){
                Double valueSpecial;
                while(true){
                    valueSpecial=specialChromosomes.get(i)+((Math.random()*300.0)-150.0);
                    boolean cond=true;
                    if(valueSpecial<0.0) cond=false;
                    if(valueSpecial>2500.0) cond=false;
                    if(cond) break;
                }
                specialChromosomes.set(i,valueSpecial);
            }
        }
        body.setForces(forcesChromosomes);
        body.setAngles(anglesChromosomes);
        body.setTimesToActivateSpecial(specialChromosomes);
    }
    public void createNextGeneration(){
        for(int i=0; i<((bodyLength/2)-1);i++){
            boolean selectAnglesChromosomesOfBodiesSelected[]=new boolean[sizeOfForcesAnglesAndSpecial];
            boolean selectForcesChromosomesOfBodiesSelected[]=new boolean[sizeOfForcesAnglesAndSpecial];
            boolean selectSpecialChromosomesOfBodiesSelected[]=new boolean[sizeOfForcesAnglesAndSpecial];
            for(int j=0; j<sizeOfForcesAnglesAndSpecial;j++){
                if(Math.random()<0.5)
                    selectAnglesChromosomesOfBodiesSelected[j]=true;
                else
                    selectAnglesChromosomesOfBodiesSelected[j]=false;

                if(Math.random()<0.5)
                    selectForcesChromosomesOfBodiesSelected[j]=true;
                else
                    selectForcesChromosomesOfBodiesSelected[j]=false;

                if(Math.random()<0.5)
                    selectSpecialChromosomesOfBodiesSelected[j]=true;
                else
                    selectSpecialChromosomesOfBodiesSelected[j]=false;
            }
            ArrayList<Body> bodiesSelected= bodiesSelectedFromRoulette();
            Body bodySelected1=bodiesSelected.get(0);
            Body bodySelected2=bodiesSelected.get(1);
     
            Body son1 =new Body();
            Body son2 =new Body();
            ArrayList<Double> forcesChromosomesOfSon1=new ArrayList<Double>();
            ArrayList<Double> forcesChromosomesOfSon2=new ArrayList<Double>();
            ArrayList<Double> anglesChromosomesOfSon1=new ArrayList<Double>();
            ArrayList<Double> anglesChromosomesOfSon2=new ArrayList<Double>();
            ArrayList<Double> specialChromosomesOfSon1=new ArrayList<Double>();
            ArrayList<Double> specialChromosomesOfSon2=new ArrayList<Double>();
            for(int j=0; j<sizeOfForcesAnglesAndSpecial;j++){
                if(selectAnglesChromosomesOfBodiesSelected[j]){
                    anglesChromosomesOfSon1.add(bodySelected1.getAngles().get(j));
                    anglesChromosomesOfSon2.add(bodySelected2.getAngles().get(j));

                }
                else{
                    anglesChromosomesOfSon1.add(bodySelected2.getAngles().get(j));
                    anglesChromosomesOfSon2.add(bodySelected1.getAngles().get(j));
                }


                if(selectForcesChromosomesOfBodiesSelected[j]){
                    forcesChromosomesOfSon1.add(bodySelected1.getForces().get(j));
                    forcesChromosomesOfSon2.add(bodySelected2.getForces().get(j));

                }
                else{
                    forcesChromosomesOfSon1.add(bodySelected2.getForces().get(j));
                    forcesChromosomesOfSon2.add(bodySelected1.getForces().get(j));
                }

                    
                if(selectSpecialChromosomesOfBodiesSelected[j]){
                    specialChromosomesOfSon1.add(bodySelected1.getTimesToActivateSpecial().get(j));
                    specialChromosomesOfSon2.add(bodySelected2.getTimesToActivateSpecial().get(j));

                }
                else{
                    specialChromosomesOfSon1.add(bodySelected2.getTimesToActivateSpecial().get(j));
                    specialChromosomesOfSon2.add(bodySelected1.getTimesToActivateSpecial().get(j));
                }                    
            }
            son1.setForces(forcesChromosomesOfSon1);
            son1.setAngles(anglesChromosomesOfSon1);
            son1.setTimesToActivateSpecial(specialChromosomesOfSon1);
            son2.setForces(forcesChromosomesOfSon2);
            son2.setAngles(anglesChromosomesOfSon2);
            son2.setTimesToActivateSpecial(specialChromosomesOfSon2);
            mutationBody(son1);
            mutationBody(son2);
            bodiesUnused.add(son1);
            bodiesUnused.add(son2);
        }
        for(int cont=0;cont<1;cont++){
            Body body=new Body();
            ArrayList<Double> forces= new ArrayList<Double>();
            ArrayList<Double> angles= new ArrayList<Double>();
            ArrayList<Double> special= new ArrayList<Double>();
            for(int j=0;j<sizeOfForcesAnglesAndSpecial;j++){
                forces.add(Math.random()*45.0);
                angles.add(Math.random()*80.0);
                special.add(Math.random()*2500.0);
            }
            body.setForces(forces);
            body.setAngles(angles);           
            body.setTimesToActivateSpecial(special);
            bodiesUnused.add(body);
        }
        Body b=bodiesUsed.get(0);
        for(int cont=0;cont<bodiesUsed.size();cont++){
            if(b.getScore()<bodiesUsed.get(cont).getScore()){
                b=bodiesUsed.get(cont);
            }
        }
        Body body=new Body();
        body.setForces(b.getForces());
        body.setAngles(b.getAngles());
        body.setTimesToActivateSpecial(b.getTimesToActivateSpecial());
        body.setId(b.getId());
        bodiesUnused.add(body);
        bodiesUsed.clear();
    }
    public boolean readyToCreateNextGeneration(){
        if(bodyAngleSelected.size()==0 || bodyForceSelected.size()==0 || bodySpecialSelected.size()==0 ) {
            if(bodiesUnused.size()==0){
                return true;
            }
        }
        return false;
    }
    public void restoreForceAngleAndSpecial(Double force,Double angle, Double special){
        bodyForceSelected.add(0,force);
        bodyAngleSelected.add(0,angle);
        bodySpecialSelected.add(0,special);
    }
    public double[] getForceAngleAndSpecial(){
        System.out.println("get force, angle special:"+ bodiesUnused.size());
        System.out.println("get force, angle and special:"+ bodyAngleSelected.size());
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
    public int getBodyLength(){
        return bodyLength;
    }
    public int getGeneration(){
        return generation;
    }
}
