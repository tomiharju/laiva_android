package com.me.Battleships;

import GameLogic.GameLogicHandler;
import Utilities.Turn;


public class Dispatcher extends Thread {
	private GameLogicHandler handler;
	private boolean messageWaiting;
	private Turn turn;
	public Dispatcher(){
		messageWaiting=false;
	}
	
	@Override
	public void run(){
		while(true){
			try {
			sleep(1000);
			if(messageWaiting){
				switch(turn.type){
				case Turn.TURN_START:
					System.out.println("Starting new round!");
					handler.receiveTurn(new Turn(Turn.TURN_START));
					break;
					
				case Turn.TURN_SHOOT:
					handler.receiveTurn(turn);
					break;
				
				case Turn.TURN_RESULT:
					handler.receiveTurn(turn);
					break;
				
				default:
					System.out.println("Unhandler message "+turn.type);
					break;
				}
				messageWaiting=false;
				
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	}

	public void sendTurn(Turn t) {
		//Server.sendmessage;
		
		switch(t.type){
		case Turn.TURN_READY:
			System.out.println("Dispatc: receive Ready");
			receiveTurn(new Turn(Turn.TURN_START));
			break;
		case Turn.TURN_SHOOT:
			receiveTurn(new Turn(Turn.TURN_SHOOT,(float)(Math.random()*10),(float) (2.5+(int)(Math.random()*10)),(int) (Math.random()*4)));
			break;
		case Turn.TURN_RESULT:
			System.out.println("Dispatc: receive Start");
			receiveTurn(new Turn(Turn.TURN_START));
			break;
		default:
			System.out.println("Unhandled message "+t.type);
			break;
		}
	
	}
	public void receiveTurn(Turn t){
		turn = t;
		messageWaiting=true;
		
	}
	
	

	public void setLogicHandler(GameLogicHandler h) {
		this.handler=h;
		
	}

	

}
