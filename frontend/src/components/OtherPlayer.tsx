import React from 'react'
import './OtherPlayer.css'
import PlayingCard from './PlayingCard'

interface Props{
    ID:number; //if ID is 0, then the option for a bot is available
    USERNAME: string;
    chips: number;
    image: string;
    faceup: boolean;
}

const OtherPlayer = ({ID, USERNAME, chips, image, faceup} : Props) => {

    if(ID){ //if there is a player there:
        const action:string = "fold";

        const playercard:React.ReactNode = 
        <div className="card w-auto fs-6 ">
            <img src={image} className="card-img-top" alt="pepe the frog pfp"/>
            <ul className="list-group list-group-flush">
                <li className="list-group-item">{USERNAME}</li>
                <li className="list-group-item">{chips}</li>
                <li className="list-group-item">{"position?"}</li>
            </ul>
        </div>;
        const cards:React.ReactNode = 
        <div className = "other-player-action">
            <div className="hand">
                <PlayingCard faceup = {faceup} type = {"other"} value ={10} suit={1}/>
                <PlayingCard faceup = {faceup} type = {"other"} value ={10} suit={1}/>
            </div>
            <div className = "action">
                <div className="card fs-6 ">
                    <div className="card-body p-1">
                        <p className="card-text">{action}</p>
                    </div>
                </div>
            </div>
        </div>;

        return(
            <div className="other-player-display">
                <div>{playercard}</div>
                <div>{cards}</div>
            </div>
            
        )
    } else {
        return (
            <button>add bot</button>
        )
    }
}

export default OtherPlayer;