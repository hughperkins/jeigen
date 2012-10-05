// Copyright Hugh Perkins 2012, hughperkins -at- gmail
//
// This Source Code Form is subject to the terms of the Mozilla Public License, 
// v. 2.0. If a copy of the MPL was not distributed with this file, You can 
// obtain one at http://mozilla.org/MPL/2.0/.

package jeigen;

public class Timer {
	long startTime;
	long lastTime;
	
	public Timer(){
		lastTime = System.nanoTime();	
		startTime = lastTime;
	}
	
	public void timeCheck(String message){
		long thistime = System.nanoTime();
		long elapsednano = thistime - lastTime;
		int milliseconds = (int)(elapsednano / 1000 / 1000);
//		if( StaticConfig.getInstance().debug ) {
			System.out.println(message + ": " + milliseconds + " milliseconds Total elapsed " + (int)((thistime - startTime) / 1000 / 1000) );
//		}
		lastTime = thistime;
	}
	
	public int totalTimeMilliseconds(){
		long thistime = System.nanoTime();
		return (int)((thistime - startTime) / 1000 / 1000);
	}
	
	public int timeCheckMilliseconds(){
		long thistime = System.nanoTime();
		long elapsednano = thistime - lastTime;
		int milliseconds = (int)(elapsednano / 1000 / 1000);
		lastTime = thistime;
		return milliseconds;
	}
	public void printTimeCheckMilliseconds(){
		System.out.println(timeCheckMilliseconds() + " ms");
	}
	public void printTimeCheckMilliseconds(String message){
		System.out.println(message + ": " + timeCheckMilliseconds() + " ms");
	}
}
