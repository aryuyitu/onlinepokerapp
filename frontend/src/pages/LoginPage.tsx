import React, { useState } from 'react';
import playerApi from '../api/playerApi';
import "./loginPage.css"
import { ErrorCodes } from '../errors/errorCodes';
import { useHistory } from 'react-router-dom';

const LoginPage: React.FC = () => {
  const [username, setUsername] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [error, setError] = useState<string>('');
  const [playerID, setPlayerID] = useState<number>(0);

  const history = useHistory();

  const handleUsernameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUsername(e.target.value);
  };

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    // Basic validation
    if (username === '' || password === '') {
      setError('Both fields are required');
      return;
    }
    try {
      console.log('trying to login...');
      await playerApi.login(username,password).then((data) => {
        console.log(data);
        setPlayerID(data);
        if(data > 0){
          history.push("./game-selector");
        }
      });
    } catch (err) {
      setPlayerID(-2);
    }
    console.log('Username:', username);
    console.log('Password:', password);
  };

  return (
    <div className="main">
    <div className="main-container">
      <h3>Login to poker app</h3>
      {error && <p className="error">{error}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="username">Username:</label>
          <input
            type="text"
            id="username"
            value={username}
            onChange={handleUsernameChange}
          />
        </div>
        <div className="input-section">
          <label htmlFor="password">Password:</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={handlePasswordChange}
          />
        </div>
        <button className="submit-button" type="submit">Login</button>
        {playerID < 0 && (
          <div>
            {ErrorCodes.get(playerID)}
          </div>
        )}
        <a href="./register">Don't have an account? Register here!</a>
      </form>
    </div>
    </div>
  );
};

export default LoginPage;