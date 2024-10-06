import React from 'react';
//import logo from './logo.svg';
import '../App.css';
import PlayingCard from '../components/PlayingCard';

import OtherPlayer from '../components/OtherPlayer';

interface Player{
  playerID: number;
  username: string;
  chipReserve: number;
  gameID: number;
}

interface Game{ //if i want to do smth with this i need to make a const and smth chaeck chatgpt
  players: Player[];
  gameID: number;
  player1chips: number;
  player2chips: number;
  player3chips: number;
  player4chips: number;
  player5chips: number;
}

export default function GamePage(){
    return (
        <div className="App">
          <header className="App-header">
            
          </header>
          <div className="main">
            <div className="poker-table">
              <div className="seat seat2"><OtherPlayer ID={1} USERNAME={"alequander"} chips={1000} image={"./images/pepe.jpg"} faceup = {false}/></div>
              <div className="seat seat4"><OtherPlayer ID={2} USERNAME={"gerber"} chips={250} image={"./images/pepe.jpg"}faceup = {false}/></div>
              <div className="seat seat5"><OtherPlayer ID={3} USERNAME={"qscguko"} chips={1500} image={"./images/pepe.jpg"}faceup = {false}/></div>
              <div className="seat seat6"><OtherPlayer ID={4} USERNAME={"murdershards"} chips={1250} image={"./images/pepe.jpg"}faceup = {false}/></div>
              <div className="playerUI">
                <div className="playerCards">
                  <PlayingCard faceup = {true} type="player" value={9} suit={1} />
                  <PlayingCard faceup = {true} type="player" value={9} suit={2} />
                </div>
                <div className="action-bar">
                  <button>fold</button>
                  <button>check</button>
                  <button>raise</button>
                </div> 
              </div>
              <div className="boardCards">
                <PlayingCard faceup = {false} type="board" value={10} suit={4} />
                <PlayingCard faceup = {true} type="board" value={11} suit={4} />
                <PlayingCard faceup = {true} type="board" value={12} suit={4} />
                <PlayingCard faceup = {true} type="board" value={13} suit={4} />
                <PlayingCard faceup = {true} type="board" value={14} suit={4} />
              </div>
            </div>
          </div>
        </div>
      );
}

export {};