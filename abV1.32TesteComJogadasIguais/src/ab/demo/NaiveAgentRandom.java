/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2014, XiaoYu (Gary) Ge, Stephen Gould, Jochen Renz
 **  Sahan Abeyasinghe,Jim Keys,  Andrew Wang, Peng Zhang
 ** All rights reserved.
**This work is licensed under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
**To view a copy of this license, visit http://www.gnu.org/licenses/
 *****************************************************************************/
package ab.demo;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.utils.StateUtil;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
public class NaiveAgentRandom implements Runnable {

	private ActionRobot aRobot;
	private Random randomGenerator;
	public int currentLevel = 1;
	public static int time_limit = 12;
	private Map<Integer,Integer> scores = new LinkedHashMap<Integer,Integer>();
	TrajectoryPlanner tp;
	private boolean firstShot;
	private Point prevTarget;
    int accumulatedScoreCount=0;
	// a standalone implementation of the Naive Agent
	public NaiveAgentRandom() {
		
		aRobot = new ActionRobot();
		tp = new TrajectoryPlanner();
		prevTarget = null;
		firstShot = true;
		randomGenerator = new Random();
		// --- go to the Poached Eggs episode level selection page ---
		ActionRobot.GoFromMainMenuToLevelSelection();

	}

	
	// run the client
	public void run() {
        try{
            System.out.println("Iniciando robo");
            aRobot.loadLevel(currentLevel);
            ArrayList<Double> input= new ArrayList<Double>();
            FileWriter arq = new FileWriter("randomDataBase",true);
            PrintWriter gravarArq = new PrintWriter(arq);
            
            while (true) {                        
                double angle=Math.random()*60;
                double strength=Math.random()*45;
                input.add(new Double(angle));
                input.add(new Double(strength));
                GameState state = solve(angle,strength);            
                System.out.println("Forca:"+strength);
                System.out.println("angulo:"+angle);
                strength=strength*1.30;
                
                int accumulatedTemporaryScoreCount = StateUtil.getScore(ActionRobot.proxy);
                
                if(accumulatedTemporaryScoreCount>0){
                    accumulatedScoreCount=accumulatedTemporaryScoreCount;
                }
                System.out.println("score:"+accumulatedScoreCount);
                System.out.println("Status of Game:"+state);
                System.out.println("ola mundo: 0");
            
                if (state == GameState.WON ||  state == GameState.LOST) {
                    System.out.println("ola mundo: 1");
                    accumulatedTemporaryScoreCount = StateUtil.getScorePopUp(ActionRobot.proxy);
                    if(accumulatedTemporaryScoreCount>0 && accumulatedTemporaryScoreCount!=81000 && accumulatedTemporaryScoreCount!=600880){
                        accumulatedScoreCount=accumulatedTemporaryScoreCount;
                    }
                    System.out.println("PopUp score:"+accumulatedTemporaryScoreCount);
                    System.out.println("Total Score:"+accumulatedScoreCount);
                    if(input.size()>=10){
                        gravarArq.printf("%f@%f@%f@%f@%f@%f@%f@%f@%f@%f@%d\n", input.get(0),input.get(1),input.get(2),input.get(3), input.get(4), input.get(5), input.get(6),input.get(7),input.get(8),input.get(9),accumulatedScoreCount);
                        gravarArq.flush();
                       
                    }
                
                    accumulatedScoreCount=0;
                    input= new ArrayList<Double>();                
                    aRobot.click(410,410);
                } 
                else if (state == GameState.LEVEL_SELECTION) {
                    System.out
                        .println("Unexpected level selection page, go to the last current level : "
                                 + currentLevel);
                    aRobot.loadLevel(currentLevel);
                } else if (state == GameState.MAIN_MENU) {
                    System.out
                        .println("Unexpected main menu page, go to the last current level : "
                                 + currentLevel);
                    ActionRobot.GoFromMainMenuToLevelSelection();
                    aRobot.loadLevel(currentLevel);
                } else if (state == GameState.EPISODE_MENU) {
                    System.out
                        .println("Unexpected episode menu page, go to the last current level : "
						+ currentLevel);
                    ActionRobot.GoFromMainMenuToLevelSelection();
                    aRobot.loadLevel(currentLevel);
                }
                
            }
            //arq.close();
        }
        catch(IOException e){
        }

	}

	private double distance(Point p1, Point p2) {
		return Math
				.sqrt((double) ((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
						* (p1.y - p2.y)));
	}

	public GameState solve(double angle, double strength)
	{

		// capture Image
		BufferedImage screenshot = ActionRobot.doScreenShot();

		// process image
		Vision vision = new Vision(screenshot);

		// find the slingshot
		Rectangle sling = vision.findSlingshotMBR();

		// confirm the slingshot
        int cont=0;
		while (sling == null) {
			System.out
			.println("No slingshot detected. Please remove pop up or zoom out");
            aRobot.click(185,377);
            aRobot.click(23,180);
			ActionRobot.fullyZoomOut();
			screenshot = ActionRobot.doScreenShot();
			vision = new Vision(screenshot);
			sling = vision.findSlingshotMBR();
            cont++;
            if(cont>4) return GameState.WON;
		}
        // get all the pigs
 		List<ABObject> pigs = vision.findPigsMBR();

		GameState state = aRobot.getState();

		// if there is a sling, then play, otherwise just skip.
		if (sling != null) {

			if (!pigs.isEmpty()) {
                ActionRobot.fullyZoomOut();
				Point releasePoint = null;
				Shot shot = new Shot();
				int dx,dy;
				{
					// random pick up a pig
					ABObject pig = pigs.get(0);
					
					Point _tpt = pig.getCenter();// if the target is very close to before, randomly choose a                    
					// point near it

					prevTarget = new Point(_tpt.x, _tpt.y);

					// estimate the trajectory
                    releasePoint = tp.findReleasePoint(sling, (0.01*3.14)/2.0);

					
					// Get the reference point
					Point refPoint = tp.getReferencePoint(sling);


					//Calculate the tapping time according the bird type 
					if (releasePoint != null) {
                        Rectangle slingT = vision.findSlingshotMBR();
                        double strengthT=strength;//max strength=45
                        double angleT=-angle*(Math.PI/180.0);
                        int x=(int)(strengthT*Math.cos(angleT));
                        int y=(int)(strengthT*(-Math.sin(angleT)));
						shot = new Shot((int)slingT.getX()+8, (int)slingT.getY()+9, -x, y, 0, 100);
                        System.out.println("releasePoint x:"+slingT.getX());
                        System.out.println("releasePoint x:"+slingT.getY());
					}
					else
						{
							System.err.println("No Release Point Found");
							return state;
						}
				}

				// check whether the slingshot is changed. the change of the slingshot indicates a change in the scale.
				{
					ActionRobot.fullyZoomOut();
					screenshot = ActionRobot.doScreenShot();
					vision = new Vision(screenshot);
					Rectangle _sling = vision.findSlingshotMBR();
					if(_sling != null)
					{
						double scale_diff = Math.pow((sling.width - _sling.width),2) +  Math.pow((sling.height - _sling.height),2);
						if(scale_diff < 25)
						{
							
								aRobot.cshoot(shot);
                                aRobot.click(30,30);
                                int accumulatedTemporaryScoreCount = StateUtil.getScore(ActionRobot.proxy);
                                if(accumulatedTemporaryScoreCount>0){
                                    accumulatedScoreCount=accumulatedTemporaryScoreCount;
                                }
                                aRobot.click(30,30);
                                aRobot.click(23,180);
								state = aRobot.getState();
								if ( state == GameState.PLAYING )
								{       
                
	
									screenshot = ActionRobot.doScreenShot();
									vision = new Vision(screenshot);
									List<Point> traj = vision.findTrajPoints();
									//tp.adjustTrajectory(traj, sling, releasePoint);
									firstShot = false;
								}
							
						}
						else
							System.out.println("Scale is changed, can not execute the shot, will re-segement the image");
					}
					else
						System.out.println("no sling detected, can not execute the shot, will re-segement the image");
				}

			}

		}
		return state;
	}

	public static void main(String args[]) {

		NaiveAgent na = new NaiveAgent();
		if (args.length > 0)
			na.currentLevel = Integer.parseInt(args[0]);
		na.run();

	}
}
