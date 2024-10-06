import "./PlayingCard.css" //do i need this?
import React from 'react'
import { GiClubs,GiDiamonds, GiHearts, GiSpades } from "react-icons/gi";

interface Props{
    faceup: boolean;
    type: string; // this is to figure out whos holding the card
    value: number; // 14 for ace? 13 for king, 12 for queen, 11 for jack // 14 would be nice for high card comparisons; 0 if facedown
    suit: number; // 1 for spade, 2 for diamonds, 3 for clubs, 4 for hearts // even are red, odd are black
}

const suitComponents: Record<number, React.ReactNode> = {
    1: <GiSpades />,
    2: <GiDiamonds />,
    3: <GiClubs />,
    4: <GiHearts />,
};

const cardStrings: Record<number, string> = {
    2: "2",
    3: "3",
    4: "4",
    5: "5",
    6: "6",
    7: "7",
    8: "8",
    9: "9",
    10: "10",
    11: "J",
    12: "Q",
    13: "K",
    14: "A",
};

const PlayingCard = ({faceup, type, value, suit} : Props) => { 
    let cardColor:string = 'red';
    let val:string = cardStrings[value];
    const SuitComponent = suitComponents[suit];
    if (faceup) {
        if(suit%2){
            cardColor = 'black';
        } 
        return (
            <div className={"playingCard front " + cardColor + " " + type}>
                <p>{SuitComponent}{val}</p>
            </div>
        )
    } else { //this is if facedown
        return (
            <div className={"playingCard back " + type}>
                <p>back</p>
            </div>
        )
    }
}



export default PlayingCard;
