package com.viral.omshiv.Settings.History;

public class History_support {
	private String Start_time;
    private String finish_Time;
    private String CaloriesBurn;
    private String DistanceCovered;
    private String stepTaken;
    private String ElapsedTime;


    public String getDistanceCovered(){

        return DistanceCovered;
    }

    public void setDistance_Covered(String App_Date){

        this.DistanceCovered = App_Date;
    }
    public String getStepTaken(){

        return stepTaken;
    }

    public void setStep_Taken(String doctor_name){

        this.stepTaken = doctor_name;
    }

    public String getElapsedTime(){

        return ElapsedTime;
    }

    public void setElapsed_Time(String patient_medication){

        this.ElapsedTime = patient_medication;
    }


    public String getStart_time() {
		return Start_time;
	}


	public void setStart_Time(String P_ID) {
		this.Start_time = P_ID;
	}

	public String getFinish_Time() {
		return finish_Time;
	}

	public void setFinish_Time(String Pa_Name) {
		this.finish_Time = Pa_Name;
	}

	public String getCaloriesBurn() {
		return CaloriesBurn;
	}

	public void setCalories_Burn(String Pa_DatofBirth) {
		this.CaloriesBurn = Pa_DatofBirth;
	}

}