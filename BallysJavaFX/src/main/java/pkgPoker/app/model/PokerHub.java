package pkgPoker.app.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import netgame.common.Hub;
import pkgPokerBLL.Action;
import pkgPokerBLL.Card;
import pkgPokerBLL.CardDraw;
import pkgPokerBLL.Deck;
import pkgPokerBLL.GamePlay;
import pkgPokerBLL.GamePlayPlayerHand;
import pkgPokerBLL.Player;
import pkgPokerBLL.Rule;
import pkgPokerBLL.Table;

import pkgPokerEnum.eAction;
import pkgPokerEnum.eCardDestination;
import pkgPokerEnum.eDrawCount;
import pkgPokerEnum.eGame;
import pkgPokerEnum.eGameState;

public class PokerHub extends Hub {

	private Table HubPokerTable = new Table();
	private GamePlay HubGamePlay;
	private int iDealNbr = 0;

	public PokerHub(int port) throws IOException {
		super(port);
	}

	protected void playerConnected(int playerID) {

		if (playerID == 2) {
			shutdownServerSocket();
		}
	}

	protected void playerDisconnected(int playerID) {
		shutDownHub();
	}

	protected void messageReceived(int ClientID, Object message) {

		if (message instanceof Action) {
			Player actPlayer = (Player) ((Action) message).getPlayer();
			Action act = (Action) message;
			switch (act.getAction()) {
			case Sit:
				HubPokerTable.AddPlayerToTable(actPlayer);
				resetOutput();
				sendToAll(HubPokerTable);
				break;
			case Leave:			
				HubPokerTable.RemovePlayerFromTable(actPlayer);
				resetOutput();
				sendToAll(HubPokerTable);
				break;
			case TableState:
				resetOutput();
				sendToAll(HubPokerTable);
				break;
			case StartGame:
				// Get the rule from the Action object.
				Rule rle = new Rule(act.geteGame());
				Player dealer = actPlayer;
				
				//TODO Lab #5 - If neither player has 'the button', pick a random player
				//		and assign the button.		
				

				//TODO Lab #5 - Start the new instance of GamePlay
				
				HubGamePlay = new GamePlay(rle, dealer.getPlayerID());
				
				for (Player p : HubPokerTable.getHmPlayer().values()){
					
					HubGamePlay.addPlayerToGame(p);
				}
								
				// Add Players to Game
				
				// Set the order of players
				
				HubGamePlay.setiActOrder(GamePlay.GetOrder(dealer.getiPlayerPosition()));
				
				act.setAction(eAction.Draw);
				


			case Draw:

				//TODO Lab #5 -	Draw card(s) for each player in the game.
				//TODO Lab #5 -	Make sure to set the correct visibility
				//TODO Lab #5 -	Make sure to account for community cards
				
				this.iDealNbr+=1;
				
				for(int i = 1; i <= rle.GetMaxDrawCount(); i++ ){
					
					if(i == 1) {
						
						rle.GetDrawCard(eDrawCount.FIRST);
					HubGamePlay.seteDrawCountLast(eDrawCount.SECOND);
					}
					
					else if(i == 2) {
						rle.GetDrawCard(eDrawCount.SECOND);
					HubGamePlay.seteDrawCountLast(eDrawCount.THIRD);
					}
					
					else if(i == 3) {
						rle.GetDrawCard(eDrawCount.THIRD);
					HubGamePlay.seteDrawCountLast(eDrawCount.FOURTH);
					}
					
					else if(i == 4) {
						
						rle.GetDrawCard(eDrawCount.FOURTH);
					HubGamePlay.seteDrawCountLast(eDrawCount.FIFTH);
					
					}
					
					else if(i == 5) {
						
						rle.GetDrawCard(eDrawCount.FIFTH);
					HubGamePlay.seteDrawCountLast(eDrawCount.SIXTH);
					}
					
					else {
						
						rle.GetDrawCard(eDrawCount.SIXTH);
					HubGamePlay.seteDrawCountLast(eDrawCount.SEVENTH);
					
					}
				}

				//TODO Lab #5 -	Check to see if the game is over
				HubGamePlay.isGameOver();
				
				resetOutput();
				//	Send the state of the gameplay back to the clients
				sendToAll(HubGamePlay);
				break;
			case ScoreGame:
				// Am I at the end of the game?

				resetOutput();
				sendToAll(HubGamePlay);
				break;
			}
			
		}

	}

}