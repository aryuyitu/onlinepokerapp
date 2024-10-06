import React, { useEffect, useState } from 'react';
import playerApi from '../api/playerApi';
import "./loginPage.css"
import { useHistory } from 'react-router-dom';

interface playerInfo {
    id:number;
    username: string,
    chipReserve: number,
}

const GameSelectorPage: React.FC = () => {
    const [playerData, setPlayerData] = useState<playerInfo | null>(null);

    const history = useHistory();

    useEffect(() => { // runs when this page is loaded
        const temp = sessionStorage.getItem('playerInfo')
        if (temp !== null){
            setPlayerData(JSON.parse(temp));
        } else { // if they got here by accident, send them to the login screen
            history.push('./login');
        }
    }, []);

    return (
    <div className='main'>
        {playerData && (
            <div>
                <h1>Welcome to poker app {playerData.username}</h1>
                <h3>Your current chip reserve is {playerData.chipReserve}</h3>
            </div>
        )}
        <div className='main-container'>
            <button>join a random game</button>
            <button>join game with code</button>
        </div>
    </div>
    );
};

export default GameSelectorPage;