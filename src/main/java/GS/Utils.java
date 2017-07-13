package GS;

import java.util.ArrayList;
import java.util.List;

public class Utils {
	private Integer revs = 0;
	private final Float perimiter = 2.1f;
	private int measurementCounter = 0;
	private Float velocity = 0.0f;
	private List<Integer> sensorRevolutions = new ArrayList<Integer>();
	
	public void setRevolutions(StringBuffer revs){
		this.revs = Integer.parseInt(revs.substring(revs.indexOf(":") + 1, revs.length()).trim());
	}
	
	public Integer getRevolutions (){
		return this.revs;
	}
	
	public List<Integer> collectData(StringBuffer revolutions){
		this.setRevolutions(revolutions);
		Integer revNumber = this.getRevolutions();
		//Integer revNumber = Integer.parseInt(revolutions.substring(revolutions.indexOf(":") + 1, revolutions.length()).trim());

		if(sensorRevolutions.size() == 5){
			sensorRevolutions.remove(measurementCounter);
		}
		
		sensorRevolutions.add(measurementCounter, revNumber);

		measurementCounter++;
		if (measurementCounter == 5){
			measurementCounter = 0;
		}
		System.out.println("Rev list: " + sensorRevolutions.toString());
		return sensorRevolutions;
	}
	
	public Float countVelocity(List<Integer> revolutionsList){
		Float sum = 0.0f;
		if (revolutionsList.size() == 5) {
			for (Integer rev : revolutionsList) {
				sum = sum + (rev * perimiter * 3.6f);
			}
			velocity = sum / 5;
		}
		
		System.out.println("V1: " + velocity + "km/h");
		return velocity;
	}
	
	public Float countDistance(Integer revolutions){
		return revolutions * perimiter;
	}
	
	public Float countTotalDistance(Float distance, Float totalDistance){
		return totalDistance += distance;
	}
	
	public void ResetData(){
		measurementCounter = 0;
		velocity = 0.0f;
		sensorRevolutions = new ArrayList<Integer>();
	}

}
